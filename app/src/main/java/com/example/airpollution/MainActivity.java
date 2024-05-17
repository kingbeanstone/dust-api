package com.example.airpollution;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;


/** (1)
public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);

        // 대기오염 정보 API
        String api = "https://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureLIst?serviceKey=jkZr%2BH8GxnzGB9LAB%2BDG0t%2B7xV6YZeF%2BiOqlC%2Fx3%2BdTAkBnoUim7KC6DdfyDdQ3%2FqnOgQQWhWHlHyrQGOLKobw%3D%3D&returnType=xml&numOfRows=10&pageNo=1&itemCode=PM10&dataGubun=HOUR&searchCondition=MONTH";
        // API를 이용한 데이터 다운로드 객체
        DownloadWebpageTask task = new DownloadWebpageTask();
        // 데이터 다운로드 및 처리
        task.execute(api);
    }

    // 데이터 다운로드 클래스 정의
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // 백그라운드 실행 (문서 다운로드)
        @Override
        protected String doInBackground(String... urls) {
            try {
                // API에 해당하는 문서 다운로드
                String txt =  (String) downloadUrl((String) urls[0]);
                return txt;
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        // 문서 다운로드 후 자동 호출: XML 문서 출력
        protected void onPostExecute(String result) {
            tv.setText(result);
        }

        // 전달받은 API에 해당하는 문서 다운로드
        private String downloadUrl(String api) throws IOException {
            HttpURLConnection conn = null;
            try {
                // 문서를 읽어 텍스트 단위로 버퍼에 저장
                URL url = new URL(api);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                // 줄 단위로 읽어 문자로 저장
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                // 다운로드 문서 반환
                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
}
**/

/** (2)
public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);

        // 대기오염 정보 API
        String api = "https://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureLIst?serviceKey=jkZr%2BH8GxnzGB9LAB%2BDG0t%2B7xV6YZeF%2BiOqlC%2Fx3%2BdTAkBnoUim7KC6DdfyDdQ3%2FqnOgQQWhWHlHyrQGOLKobw%3D%3D&returnType=xml&numOfRows=10&pageNo=1&itemCode=PM10&dataGubun=HOUR&searchCondition=MONTH";// API를 이용한 데이터 다운로드 객체
        DownloadWebpageTask task = new DownloadWebpageTask();
        // 데이터 다운로드 및 처리
        task.execute(api);
    }

    // 데이터 다운로드 클래스 정의
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // 문서 다운로드(백그라운드 실행)
        @Override
        protected String doInBackground(String... urls) {
            try {
                // API에 해당하는 문서 다운로드
                String txt =  (String) downloadUrl((String) urls[0]);
                return txt;
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        // 문서 다운로드 후 자동 호출: MXL 문서 파싱
        protected void onPostExecute(String result) {
            boolean bSet_itemCode = false;
            boolean bSet_city = false;

            String itemCode = "";
            String pollution_degree = "";
            String tag_name = "";

            int cnt = 0;

            // ㅎ화면 초기화
            tv.setText("");

            try {
                // XML Pull Parser 객체 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                // 파싱할 문서 설정
                xpp.setInput(new StringReader(result));

                // 현재 이벤트 유형 반환(START_DOCUMENT, START_TAG, TEXT, END_TAG, END_DOCUMENT
                int eventType = xpp.getEventType();

                // 이벤트 유형이 문서 마지막이 될 때까지 반복
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    // 문서의 시작인 경우
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;

                        // START_TAG이면 태그 이름 확인
                    } else if (eventType == XmlPullParser.START_TAG) {
                        tag_name = xpp.getName();
                        if (bSet_itemCode == false && tag_name.equals("itemCode"))
                            bSet_itemCode = true;
                        if (itemCode.equals("PM10") && (tag_name.equals("seoul") || tag_name.equals("busan")))
                            bSet_city = true;

                        // 태그 사이의 문자 확인
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_itemCode) {
                            itemCode = xpp.getText();

                            if (itemCode.equals("PM10")) {
                                cnt++;
                                bSet_itemCode = false;
                            }
                        }
                        if (bSet_city) {
                            pollution_degree = xpp.getText();

                            // 도시와 미세먼지 농도 화면 출력
                            tv.append("" + cnt + ": " + tag_name + ", " + pollution_degree + "\n");
                            bSet_city = false;
                        }

                        // 마침 태그인 경우
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }

                    // 다음 이벤트 유형 할당
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        }

        // 전달받은 API에 해당하는 문서 다운로드
        private String downloadUrl(String api) throws IOException {
            HttpURLConnection conn = null;
            try {
                // 문서를 읽어 텍스트 단위로 버퍼에 저장
                URL url = new URL(api);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                // 줄 단위로 읽어 문자로 저장
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                // 다운로드 문서 반환
                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
}
**/


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    NaverMap mMap;

    // 도시명
    String[] city = {"서울", "부산"};
    // 좌표
    LatLng[] latlng = new LatLng[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mMap = naverMap;
        naverMap.setMapType(NaverMap.MapType.Basic);

        // 서울 좌표
        latlng[0] = new LatLng(37.566506, 126.977977);
        // 부산 좌표
        latlng[1] = new LatLng(35.184912, 129.076744);

        // 지도 중심: 서울과 부산 사이
        LatLng latlng_center = new LatLng((latlng[0].latitude + latlng[1].latitude)/2, (latlng[0].longitude + latlng[1].longitude)/2);
        CameraUpdate cameraUpdate1 = CameraUpdate.scrollTo(latlng_center);
        naverMap.moveCamera(cameraUpdate1);

        // 지도 크기
        CameraUpdate cameraUpdate2 = CameraUpdate.zoomTo(6);
        naverMap.moveCamera(cameraUpdate2);

        // 대기오염 정보 API
    String api = "https://apis.data.go.kr/B552584/ArpltnStatsSvc/getCtprvnMesureLIst?serviceKey=jkZr%2BH8GxnzGB9LAB%2BDG0t%2B7xV6YZeF%2BiOqlC%2Fx3%2BdTAkBnoUim7KC6DdfyDdQ3%2FqnOgQQWhWHlHyrQGOLKobw%3D%3D&returnType=xml&numOfRows=10&pageNo=1&itemCode=PM10&dataGubun=HOUR&searchCondition=MONTH";
        // API를 이용한 데이터 다운로드 객체
        DownloadWebpageTask task = new DownloadWebpageTask();
        // 데이터 다운로드 및 처리
        task.execute(api);
    }

    // 데이터 다운로드 클래스 정의
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        // 문서 다운로드(백그라운드 실행)
        @Override
        protected String doInBackground(String... urls) {
            try {
                // API에 해당하는 문서 다운로드
                String txt =  (String) downloadUrl((String) urls[0]);
                return txt;
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }


        // 문서 다운로드 후 자동 호출: MXL 문서 파싱
        protected void onPostExecute(String result) {
            boolean bSet_itemCode = false;
            boolean bSet_city = false;

            String itemCode = "";
            String pollution_degree = "";
            String tag_name = "";

            int cnt = 0;
            int city_no = 0;

            try {
                // XML Pull Parser 객체 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                // 파싱할 문서 설정
                xpp.setInput(new StringReader(result));

                // 현재 이벤트 유형 반환(START_DOCUMENT, START_TAG, TEXT, END_TAG, END_DOCUMENT
                int eventType = xpp.getEventType();

                // 이벤트 유형이 문서 마지막이 될 때까지 반복
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // 문서의 시작인 경우
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;

                        // START_TAG이면 태그 이름 확인
                    } else if (eventType == XmlPullParser.START_TAG) {
                        tag_name = xpp.getName();
                        if (bSet_itemCode == false && tag_name.equals("itemCode"))
                            bSet_itemCode = true;
                        if (itemCode.equals("PM10") &&
                                (tag_name.equals("seoul") || tag_name.equals("busan")))
                            bSet_city = true;

                        // 태그 사이의 문자 확인
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_itemCode) {
                            itemCode = xpp.getText();

                            if (itemCode.equals("PM10")) {
                                cnt++;
                                bSet_itemCode = false;

                                // PM10에 대한 가장 가까운 시간 대의 도시 대기오염 정보 추출 후에 반복 종료
                                if (cnt > 1)
                                    break;
                            }
                        }
                        if (bSet_city) {
                            pollution_degree = xpp.getText();

                            // 지도 위에 해당 도시의 미세먼지 농도 표시
                            addInfo(latlng[city_no], city[city_no], pollution_degree);

                            city_no++;
                            bSet_city = false;
                        }

                        // 마침 태그인 경우
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }

                    // 다음 이벤트 유형 할당
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        }

        // 전달받은 API에 해당하는 문서 다운로드
        private String downloadUrl(String api) throws IOException {
            HttpURLConnection conn = null;
            try {
                // 문서를 읽어 텍스트 단위로 버퍼에 저장
                URL url = new URL(api);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                // 줄 단위로 읽어 문자로 저장
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                // 다운로드 문서 반환
                return page;
                // } catch (Exception e) {
                //      return e.getMessage();
            } finally {
                conn.disconnect();
            }
        }
    }

    // 지도 위에 미세먼지 농도 표시
    public void addInfo(LatLng latlng, String city, String pollution_degree) {
        final String polution = pollution_degree;

        Marker marker = new Marker();
        marker.setPosition(latlng);
        marker.setMap(mMap);

        marker.setSubCaptionText(city);
        marker.setSubCaptionColor(Color.RED);
        marker.setSubCaptionHaloColor(Color.YELLOW);
        marker.setSubCaptionTextSize(20);

        InfoWindow infoWindow1 = new InfoWindow();
        infoWindow1.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return polution;
            }
        });
        infoWindow1.open(marker);
    }
}




