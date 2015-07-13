package com.example.exam.mjm.android.network.activity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ExamNetworkActivity_01 extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       findViewById(R.id.button1).setOnClickListener(this);
    }
//
 public void onClick(View v) {
  TextView tv = (TextView)findViewById(R.id.textView1);
  try {
   String html = loadKmaData();
   
   //DOM �Ľ�.
        ByteArrayInputStream bai = new ByteArrayInputStream(html.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //dbf.setIgnoringElementContentWhitespace(true);//ȭ��Ʈ�����̽� ����
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document parse = builder.parse(bai);//DOM �ļ�
        //�±� �˻�fdfdfdf
        NodeList datas = parse.getElementsByTagName("data");
        //String result = "data�±� �� =" + datas.getLength()+"\n";
        String result = "";
        //17���� data�±׸� ������ ����
        for (int idx = 0; idx < datas.getLength(); idx++) {
         //�ʿ��� �������� ���� ���� ����
         String day = "";
         String hour = "";
         String sky = "";
         String temp = "";
         Node node = datas.item(idx);//data �±� ����
         int childLength = node.getChildNodes().getLength();
         //�ڽ��±� ��� ����
         NodeList childNodes = node.getChildNodes();
         for (int childIdx = 0; childIdx < childLength; childIdx++) {
          Node childNode = childNodes.item(childIdx);
          @SuppressWarnings("unused")
		int count = 0;
          if(childNode.getNodeType() == Node.ELEMENT_NODE){
           count ++;
           //�±��� ��츸 ó��
           //����,����,�� ����(�ð����� ����)
           if(childNode.getNodeName().equals("day")){
            int su = Integer.parseInt(childNode.getFirstChild().getNodeValue());
            switch(su){
             case 0 : day = "����"; break; 
             case 1 : day = "����"; break; 
             case 2 : day = "��"; break; 
            }
           }else if(childNode.getNodeName().equals("hour")){
           hour = childNode.getFirstChild().getNodeValue();
           //�ϴû����ڵ� �м�
           }else if(childNode.getNodeName().equals("wfKor")){
            sky = childNode.getFirstChild().getNodeValue();
           }else if(childNode.getNodeName().equals("temp")){
            temp = childNode.getFirstChild().getNodeValue();
           }
          }
        }//end ���� for��
         result += day+" "+hour+"�� ("+sky+","+temp+"��)\n";
     }//end �ٱ��� for�� 
   tv.setText(result);
  } catch (Exception e) {
   tv.setText("����"+e.getMessage());
   e.printStackTrace();
  }
 }
 
    //���û �������� ����
 private String loadKmaData() throws Exception {
  String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=63&gridy=123";
  URL url = new URL(page);
  HttpURLConnection urlConnection =(HttpURLConnection)url.openConnection();
  if(urlConnection == null)return null;
  urlConnection.setConnectTimeout(10000);//�ִ� 10�� ���
  urlConnection.setUseCaches(false);//�Ź� �������� �о����
  StringBuilder sb = new StringBuilder();//��� ���ڿ� ����ü
  if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
   InputStream inputStream = urlConnection.getInputStream();
   InputStreamReader isr = new InputStreamReader(inputStream);
   
   //���پ� �б�
   BufferedReader br = new BufferedReader(isr);
   while(true){
    String line = br.readLine();//���������� html �ڵ� �о����
    if(line == null)break;//��Ʈ���� ������ null����
    sb.append(line+"\n");
   }//end while
   br.close();
  }//end if 
  return sb.toString();
 }
}



