# 部落客系統
這個項目是一個簡單的部落格系統，可以進行張貼文章以及撰寫評論回復留言等基本功能，如果是管理員身分的話可額外使用使用者管理、群組管理、權限管理等功能設置系統 <br>
另外頁面上參照如dcard的一些頁面設計 可以在使用者資料上查看當前張貼文章數、評論數、按讚數等訊息，系統也能進行全文搜尋 利用文章內文 標題 作者等進行查詢 <br>
當使用者收藏其他作者的文章時 若該作者更改其文章內容或發佈新文章時，可從注冊mail中收到通知 利用kafka 線性序傳輸的特性 可保證資料不丟失 且
功能還在持續擴充中

擴充功能
- 作者追蹤
- 統計分析: 依照使用者權限顯示不同統計分析紀錄

### 項目使用技術處理點
- 使用springboot aop 用動態代理 對指定功能進行增強，再透過kafka傳輸寄送郵件訊息，透過kafka線性序列傳輸特點，可提高傳輸速度以及保證資料不丟失，確保郵件傳遞效率 <br>
- 使用websocket進行雙向傳輸，使用場景: 文章按讚收藏、評論點讚等功能，為了避免client端多次請求數據，採用websocket進行雙向傳輸 及時回傳案讚總數 收藏數等數據 <br>
- 使用dockerfile和docker-compose 快速部屬項目所需環境
- 因應jwt無狀態的情況，為避免被有心人士擷取登入，設置黑名單並存放置redis中進行校驗
- 避免登入時訪問資料庫次數過多，使用緩存quava-cache存儲使用者資訊，減少資料庫訪問次數
<hr>

## 系統介紹：
使用springboot + vue 的前後端分離項目，使用spring-security進行功能的權限驗證管理、因項目是採取前後分離因此設定可採不同源訪問 <br>
前端採用vite搭建vue3項目 搭配vuetify進行前端頁面撰寫、使用pinia、router 進行路由管理以及使用者資料全局狀態管理 <br>
後端使用springboot搭建spring項目、使用restful建置controller功能項，並且統一管理錯誤處理、回應物件處理等增加統一性 <br>
搭配docker、dockerfile、docker-compos 進行容器化處理 以及maven-plugin 進行docker-build push等處理 上傳項目至docker-hub <br>

前端項目網址: https://github.com/yaiiow159/blog-frontend

目前正在進行: 撰寫前端後端的CI/CD流程、部屬GCP上 <br>

## 系統流程說明 (構思說明)
前端使用axios來傳遞前端資料至後端，後端使用dto接收資料，並使用mapstructer來轉換dto至po物件並在dao層進行crud操作<br>
系統可以針對容器化配置以及本地化配置有分別使用不同配置檔進行設定

流程圖: <br>
![blog_app系統流程圖 drawio](https://github.com/yaiiow159/Blog_app/assets/39752246/5c30b0b1-34f4-4314-b819-e65416abdecc)


資料庫規劃:
![Diagram 1](https://github.com/yaiiow159/Blog_app/assets/39752246/d73d894b-28b6-4dd0-9bc1-437d94935b34)

---------------------------------------------------------------------------------------------------------------------
## 使用技術介紹:
<hr>

前端使用技術: vue3 + pinia(全局狀態管理) + sass + router(路由管理) + axios(前後端資料傳遞) <br>
後端使用技術: springboot3 (spring配置框架) + spring-security(權限控制) + spring-data-jpa(dao層操作) + spring-scheduled(定時任務) + springboot-mail(郵件發送)<br>
消息隊列: kafka + zookeeper <br>
資料庫:mysql <br>
非關係型資料庫:redis <br>
文檔生成以及測試: openApi3 <br>
容器化: docker、docker-compose <br>
容器化管理平台: portainer <br>
雲服務技術: GCP(google-storage、computer-engine、Artifact-registry)

---------------------------------------------------------------------------------------------------------------------

## OpenApi3 測試後端Api 以及說明文檔
因應項目是前後端分離項目，導入openApi 可以針對後端Api進行測試 驗證回傳結果 測試前須先使用jwt token開放權限 <br>

### openApi測試相關網址
http://localhost:9090/swagger-ui/index.html <br>
http://localhost:9090/v3/api-docs <br>

### swagger測試相關
![openApi測試](https://github.com/yaiiow159/Blog_app/assets/39752246/ff09ccd0-4a63-4333-a8ec-50fd9a5ea3a1) <br>
![openApi測試開放](https://github.com/yaiiow159/Blog_app/assets/39752246/33fbb056-92bb-4441-9a38-586190a5007c) <br>

![openApi Dto說明](https://github.com/yaiiow159/Blog_app/assets/39752246/eb0226d2-a0d6-4716-9738-8712fe1bff06) <br>
<hr>

## 容器化
項目有針對容器化環境進行項目配置，可使用dockerFile搭配docker-compose 集成 搭建項目所需環境，並搭配portainer檢控容器狀況
此構思是因應快速部屬環境以及部屬至雲服務
![docker照片](https://github.com/yaiiow159/Blog_app/assets/39752246/3b15dde8-e3e7-4d92-bb9a-312f59df606c)

容器化項目正常運行
![docker項目正常運行](https://github.com/yaiiow159/Blog_app/assets/39752246/aaa0474f-3a29-4606-bc65-dd1b89a9c47c)

<hr>

## 畫面預覽:
登入畫面
有進行格式校驗 以及驗證碼校驗 可註冊會員 或是忘記密碼重設等
![登入畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/34bf10dc-8c86-48c1-ad94-189ddfa007ce)

首頁畫面
![首頁畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/8c9f2caf-6736-4531-bf9c-f82c3b2c01a3)

分類管理畫面
![分類管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/60b99972-dacc-4f43-955c-5fcf680e1910)

文章管理畫面
![文章管理頁面](https://github.com/yaiiow159/Blog_app/assets/39752246/12a6a4d5-4c1a-4d26-bd4f-5d8e0d9da260)

標籤管理畫面
![標籤管理功能畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/64e39bbd-aa99-4c9c-9d26-c08512e670c8)

使用者管理畫面
![使用者管理頁面](https://github.com/yaiiow159/Blog_app/assets/39752246/17ca940a-682e-45c4-9541-43e33bbe9725)

群組管理畫面
![群組管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/75c94218-3e9a-4e0f-8f91-cbfde0c969bf)

角色管理畫面
![角色管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/4a062437-2626-43bc-8e9b-1fe12767601f)

使用者登入紀錄畫面
![使用者登入紀錄畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/e3477767-c5ca-471d-8877-820126fb150d)

以上畫面都可進行分頁功能以及針對特定欄位進行sort排序功能




