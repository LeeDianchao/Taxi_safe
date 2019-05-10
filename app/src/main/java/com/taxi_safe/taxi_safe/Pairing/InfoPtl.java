package com.taxi_safe.taxi_safe.Pairing;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class InfoPtl {
    //判断是否配对
    public boolean isPaired(String paircode) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "isPair";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("paircode", paircode);
            //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

            HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
            httpTransportSE.debug=true;
            httpTransportSE.call(namespace+methodName, envelope);//调用
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.equals("0")){
            return true;
        }else {
            return false;
        }
    }

    //获取表情
    public String getEmotion(String paircode) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "getEmotion";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("paircode", paircode);
            //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

            HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
            httpTransportSE.debug=true;
            httpTransportSE.call(namespace+methodName, envelope);//调用
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
            if(result.equals("-1")){
                return "-1";
            }else {
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
            return "-1";
        }
    }

    //获取表情
    public boolean insertEmotion(String paircode, String emotion) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "insertEmotion";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("paircode", paircode);
            request.addProperty("emotion", emotion);
            //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

            HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
            httpTransportSE.debug=true;
            httpTransportSE.call(namespace+methodName, envelope);//调用
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.equals("0")){
            return true;
        }else {
            return false;
        }
    }

    //更新表情
    public boolean updateEmotion(String paircode, String emotion) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "updateEmotion";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("paircode", paircode);
            request.addProperty("emotion", emotion);
            //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

            HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
            httpTransportSE.debug=true;
            httpTransportSE.call(namespace+methodName, envelope);//调用
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.equals("0")){
            return true;
        }else {
            return false;
        }
    }

    //删除记录
    public boolean deleteRecord(String paircode) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "deleteRecord";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("paircode", paircode);
            //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
            envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

            HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
            httpTransportSE.debug=true;
            httpTransportSE.call(namespace+methodName, envelope);//调用
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.equals("0")){
            return true;
        }else {
            return false;
        }
    }
}
