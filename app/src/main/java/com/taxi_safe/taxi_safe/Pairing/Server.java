package com.taxi_safe.taxi_safe.Pairing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

public class Server {
    private int port;
    public Server(int port){
        this.port=port;
    }
    public int getPort(){
        return this.port;
    }
}

//服务
class PairingServer extends Server{
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final int POOL_SIZE=10;

    public PairingServer(int port)throws IOException{
        super(port);
        serverSocket=new ServerSocket(port);
        executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
    }
    //直接调用此函数即开始
    public void service(){
        while (true){
            Socket socket=null;
            try {
                socket=serverSocket.accept();
                executorService.execute(new PairingServerThread(socket));
            }catch (IOException e){
                System.out.print("配对服务启动失败！");
                e.printStackTrace();
            }
        }
    }
}

class PairingServerThread implements Runnable{
    public static final String encoding="utf-8";

    private Socket socket;

    private DatabaseOP db;

    public PairingServerThread(Socket sock){
        this.socket=sock;
        this.db=new DatabaseOP();
    }

    public String generateCode(){
        Random random=new Random();
        String code="";
        for (int i=0;i<6;i++){
            code+=random.nextInt(10);
        }
        return code;
    }

    public void run(){
        try {

            InputStreamReader isr=new InputStreamReader(socket.getInputStream());
            BufferedReader br=new BufferedReader(isr);
            String str=br.readLine();
            JSONObject msg=JSONObject.parseObject(str);
            //接收到司机客户端的请求
            if (msg.getString("type").equals("driver")){
                String d_usr=msg.getString("username");
                String code=null;
                while (true) {
                    String temp = generateCode();
                    if(db.isLegal(temp,d_usr)) {
                        code = temp;
                        break;
                    }
                }
                OutputStreamWriter osw=new OutputStreamWriter(socket.getOutputStream());
                BufferedWriter bw=new BufferedWriter(osw);
                if(db.insert(d_usr,code)){
                    JSONObject back=new JSONObject();
                    back.put("pairingcode",code);
                    back.put("result","t");
                    bw.write(back.toString()+"\n");
                    bw.flush();
                    bw.close();
                    System.out.print("插入成功！");
                }
                else{
                    JSONObject back=new JSONObject();
                    back.put("result","f");
                    bw.write(back.toString()+"\n");
                    bw.flush();
                    bw.close();
                    System.out.print("插入失败！");
                }
            }
            //接收到乘客客户端的请求
            else if (msg.getString("type").equals("passenger")){
                String p_usr=msg.getString("username");
                String code=msg.getString("pairingcode");
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                BufferedWriter bw = new BufferedWriter(osw);
                if(db.isExist(code)){
                    if(db.insert(p_usr,code,1)) {
                        JSONObject back=new JSONObject();
                        back.put("result","t");
                        bw.write(back.toString()+"\n");
                        bw.flush();
                        bw.close();
                        System.out.print("插入成功!");
                    }else{
                        JSONObject back=new JSONObject();
                        back.put("result","f");
                        bw.write(back.toString()+"\n");
                        bw.flush();
                        bw.close();
                        System.out.print("插入失败!");
                    }
                }else{
                    JSONObject back=new JSONObject();
                    back.put("result","e");
                    bw.write(back.toString()+"\n");
                    bw.flush();
                    bw.close();
                    System.out.print("配对失败!");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

class Conn {

    public static Connection getConn() {
        String driver = "com.mysql.cj.jdbc.Driver";
        //String url = "jdbc:mysql://120.79.17.169:3306/taxi?useUnicode=true&characterEncoding=utf8";
        String url = "jdbc:mysql://120.79.17.169:3306/taxi?useUnicode=true&characterEncoding=utf8";
        String username = "root";
        String password = "123456";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn =  DriverManager.getConnection(url, username, password);
            System.out.print("连接成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.print("连接失败");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("连接失败");
        }
        return conn;
    }

}

class DatabaseOP{

    private Connection conn;
    private PreparedStatement pstmt;
    public DatabaseOP(){
        try{
            conn = Conn.getConn();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO:检查该配对码是否合法
    public boolean isLegal(String code,String p_usr,String d_usr){
        return (!isExist(code))&&(!isPaired(p_usr))&&(!isPaired(d_usr));
    }

    //TODO:检查该配对码是否合法
    public boolean isLegal(String code,String usr){
        return (!isExist(code))&&(!isPaired(usr));
    }

    //TODO:检查配对码是否已经存在
    public boolean isExist(String code){
        try {
            pstmt=conn.prepareStatement("select * from pair WHERE id=?");
            pstmt.setString(1, code);
            ResultSet rst = pstmt.executeQuery();
            if (rst.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(pstmt != null){
                    pstmt.close();
                    pstmt = null;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    //TODO:判断司机乘客是否已经配对
    public boolean isExistPassenger(String code){
        try {
            pstmt=conn.prepareStatement("select passenger_id from pair WHERE id=?");
            pstmt.setString(1,code);
            ResultSet rst = pstmt.executeQuery();
            if (rst.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(pstmt != null){
                    pstmt.close();
                    pstmt = null;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    //TODO:检查司机/乘客是否已经和人配对
    public boolean isPaired(String username){
        try {
            pstmt=conn.prepareStatement("select * from pair where driver_id=? OR passenger_id=?");
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rst = pstmt.executeQuery();
            if (rst.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(pstmt != null){
                    pstmt.close();
                    pstmt = null;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    //TODO:将司机用户名和配对码插入表中
    public boolean insert(String usrename,String code){
        try {
            String insSql = "INSERT INTO pair (id,driver_id) VALUES('" + code + "','" + usrename + "')";
            Statement st = conn.createStatement();
            st.execute(insSql);
            if(st != null){
                st.close();
                st = null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return true;
    }

    //TODO:将乘客用户名插入表中(id不用管)
    public boolean insert(String username,String code,int id){
        try {
            String insSql = "update pair set passenger_id='" + username + "' where id='" + code + "'";
            Statement st = conn.createStatement();
            st.execute(insSql);
            if (st != null) {
                st.close();
                st = null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    //TODO:用户名与配对码是否匹配
    public boolean isTrue(String username,String code){
        try {
            pstmt=conn.prepareStatement("select * from pair WHERE id=? AND passenger_id=? OR driver_id=?");
            pstmt.setString(1, code);
            pstmt.setString(2, username);
            pstmt.setString(3, username);
            ResultSet rst = pstmt.executeQuery();
            if (rst.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    //TODO:删除配对信息
    public boolean delete(String code){
        try {
            String sql = "DELETE FROM pair WHERE id='" + code + "'";

            Statement stmt = conn.prepareStatement(sql);
            int count = stmt.executeUpdate(sql);
            System.out.println("您删除了" + count + "条语句");
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    //TODO:得到当前用户的地址信息
    public Address getCurrentAddress(String username){
        Address address=new Address();
        return address;
    }

    //TODO:为用户插入地址信息
    public boolean setAddress(String username,Address address){
        return true;
    }
}

class Address{
    String ip;
    int port;
}
