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
   
   //DOM 파싱.
        ByteArrayInputStream bai = new ByteArrayInputStream(html.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //dbf.setIgnoringElementContentWhitespace(true);//화이트스패이스 생략
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document parse = builder.parse(bai);//DOM 파서
        //태그 검색fdfdfdf
        NodeList datas = parse.getElementsByTagName("data");
        //String result = "data태그 수 =" + datas.getLength()+"\n";
        String result = "";
        //17개의 data태그를 순차로 접근
        for (int idx = 0; idx < datas.getLength(); idx++) {
         //필요한 정보들을 담을 변수 생성
         String day = "";
         String hour = "";
         String sky = "";
         String temp = "";
         Node node = datas.item(idx);//data 태그 추출
         int childLength = node.getChildNodes().getLength();
         //자식태그 목록 수정
         NodeList childNodes = node.getChildNodes();
         for (int childIdx = 0; childIdx < childLength; childIdx++) {
          Node childNode = childNodes.item(childIdx);
          @SuppressWarnings("unused")
		int count = 0;
          if(childNode.getNodeType() == Node.ELEMENT_NODE){
           count ++;
           //태그인 경우만 처리
           //금일,내일,모레 구분(시간정보 포함)
           if(childNode.getNodeName().equals("day")){
            int su = Integer.parseInt(childNode.getFirstChild().getNodeValue());
            switch(su){
             case 0 : day = "금일"; break; 
             case 1 : day = "내일"; break; 
             case 2 : day = "모레"; break; 
            }
           }else if(childNode.getNodeName().equals("hour")){
           hour = childNode.getFirstChild().getNodeValue();
           //하늘상태코드 분석
           }else if(childNode.getNodeName().equals("wfKor")){
            sky = childNode.getFirstChild().getNodeValue();
           }else if(childNode.getNodeName().equals("temp")){
            temp = childNode.getFirstChild().getNodeValue();
           }
          }
        }//end 안쪽 for문
         result += day+" "+hour+"시 ("+sky+","+temp+"도)\n";
     }//end 바깥쪽 for문 
   tv.setText(result);
  } catch (Exception e) {
   tv.setText("오류"+e.getMessage());
   e.printStackTrace();
  }
 }
 
    //기상청 날씨정보 추출
 private String loadKmaData() throws Exception {
  String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=63&gridy=123";
  URL url = new URL(page);
  HttpURLConnection urlConnection =(HttpURLConnection)url.openConnection();
  if(urlConnection == null)return null;
  urlConnection.setConnectTimeout(10000);//최대 10초 대기
  urlConnection.setUseCaches(false);//매번 서버에서 읽어오기
  StringBuilder sb = new StringBuilder();//고속 문자열 결합체
  if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
   InputStream inputStream = urlConnection.getInputStream();
   InputStreamReader isr = new InputStreamReader(inputStream);
   
   //한줄씩 읽기
   BufferedReader br = new BufferedReader(isr);
   while(true){
    String line = br.readLine();//웹페이지의 html 코드 읽어오기
    if(line == null)break;//스트림이 끝나면 null리턴
    sb.append(line+"\n");
   }//end while
   br.close();
  }//end if 
  return sb.toString();
 }
}



