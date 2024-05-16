# 部落客系統
簡單的部落格系統，可以進行張貼文章以及撰寫評論回復留言等基本功能，如果是管理員身分的話可額外使用使用者管理、群組管理、權限管理等功能設置系統 <br>
可以在使用者資料上查看當前張貼文章數、評論數、按讚數等訊息，系統也能進行全文搜尋 利用文章內文 標題 作者等進行查詢 <br>
當使用者收藏其他作者的文章時 若該作者更改其文章內容或發佈新文章時，可從注冊mail中收到通知 利用kafka 線性序傳輸的特性 可保證資料不丟失 且
功能還在持續擴充中

構想功能
- 作者追蹤
- 統計分析: 依照使用者權限顯示不同統計分析紀錄

### 項目使用技術處理點
- 使用springboot aop 用動態代理 對指定功能進行增強，再透過kafka傳輸寄送郵件訊息，透過kafka線性序列傳輸特點，可提高傳輸速度以及保證資料不丟失，確保郵件傳遞效率 <br>
- 因應jwt無狀態的情況，為避免被有心人士擷取登入，設置黑名單並存放置redis中進行校驗
- 避免登入時訪問資料庫次數過多，使用緩存quava-cache存儲使用者資訊，減少資料庫訪問次數
- 使用redis 儲存驗證碼 refresh-token
- 使用redisson鎖 透過aop 進行 方法判斷 設置delay時間 避免重複提交表單內容
- 使用redis緩存 紀錄收藏數 按讚數等訊息 並設置過期時間避免數據更新 且取消緩存空值 避免緩存穿透問題
- aop 紀錄 請求地址 以及花費時間等
- 使用ApplicationEvent 監聽 Authetication 驗證狀況，如登入驗證成功，則記錄當下登入時間以及其他訊息，並保存置db中 並在登出時寫入登出時間
- 使用Jsoup 過濾前端文章內文，避免sql注入等問題
- 使用OpenApi生成文件以及後端測試接口
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
雲服務技術: GCP(google-storage、computer-engine)

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

## 登入畫面
#### 有進行格式校驗 以及驗證碼校驗 可註冊會員 或是忘記密碼重設等
![登入畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/34bf10dc-8c86-48c1-ad94-189ddfa007ce)

## 使用者資訊
可查看當前使用者 總文章數、總文章按讚數、總評論數、總按讚數等資訊
![使用者資訊](https://github.com/yaiiow159/Blog_app/assets/39752246/5ac984a7-b888-4f93-a872-b716cc6c0cd3)

## 權限判斷：
![jwy權限驗證2](https://github.com/yaiiow159/Blog_app/assets/39752246/7bc651c6-7dd4-48de-b556-2ff71d52c6ff)
![jwt權限判斷](https://github.com/yaiiow159/Blog_app/assets/39752246/ad448468-4f32-4450-8d70-6b4688e66d6d)
![驗證token過期](https://github.com/yaiiow159/Blog_app/assets/39752246/10671a34-fd72-4b01-a7cf-e1519073aa8a)
![權限驗證錯誤](https://github.com/yaiiow159/Blog_app/assets/39752246/5aeaa7ed-ce65-45fd-86c2-f82c75acfaa0)


## 重複提交檢驗
#### 後端會進行檢驗判斷 利用redission鎖機制 在delay時間會釋放鎖 如時間內進行提交 會提出錯誤訊息
![重複提交檢查](https://github.com/yaiiow159/Blog_app/assets/39752246/f0bf139f-1305-42a7-aab6-46f93aed35cb)


### 首頁畫面
![首頁閱讀文章畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/12e28a68-3ce5-4151-8e39-db1991633b89)
![首頁畫面3](https://github.com/yaiiow159/Blog_app/assets/39752246/71772b56-66fe-4cd2-92e7-4fc05ea33797)


### 分類管理畫面
![分類管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/60b99972-dacc-4f43-955c-5fcf680e1910)
![創建成功分類](https://github.com/yaiiow159/Blog_app/assets/39752246/4c73eceb-2081-4ffb-adbd-8ca86dd5e9b8)
![分類管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/1e8795ed-d0d0-4107-922f-7d79c894fed7)
![分類管理更新成功](https://github.com/yaiiow159/Blog_app/assets/39752246/77b5405f-9729-4d3f-816c-212e6234e2a7)
![刪除成功](https://github.com/yaiiow159/Blog_app/assets/39752246/d524271d-ca72-4db0-99ea-01a8e23af1ad)

### 文章管理畫面

#### 文章提供 倒攢、按讚、收藏、添加評論等功能
![文章管理](https://github.com/yaiiow159/Blog_app/assets/39752246/2ed52741-8605-4432-b94d-8257803c7dec)
![文章管理閱讀](https://github.com/yaiiow159/Blog_app/assets/39752246/10d2b284-32a9-4530-9061-eaea0d017e89)
![文章管理編輯](https://github.com/yaiiow159/Blog_app/assets/39752246/b5afb9e8-77be-4806-85a1-f86c18927a5d)
![收藏功能](https://github.com/yaiiow159/Blog_app/assets/39752246/e82247f3-8088-40cd-88c7-6a6addb5101b)
![倒贊成功](https://github.com/yaiiow159/Blog_app/assets/39752246/47c23c8f-57bc-4573-ac4f-21819acd05e4)



### 標籤管理畫面
![標籤編輯成功 - 複製](https://github.com/yaiiow159/Blog_app/assets/39752246/a776f800-2812-4fd5-9a69-edc1e0cf2cf6)
![標籤管理頁面 - 複製](https://github.com/yaiiow159/Blog_app/assets/39752246/9017ef6d-05ff-4f74-b69a-74dd2821702f)
![標籤管理功能畫面 - 複製](https://github.com/yaiiow159/Blog_app/assets/39752246/f5daee44-82b6-468f-8118-02513b319bca)
![標籤新增成功 - 複製](https://github.com/yaiiow159/Blog_app/assets/39752246/12de06cc-ae98-46f6-a1c6-af9c8e510c47)
![標籤刪除成功 - 複製](https://github.com/yaiiow159/Blog_app/assets/39752246/04f944be-5e50-49ab-aad5-304617fe83b1)

### 使用者管理畫面
![用戶管理新增成功](https://github.com/yaiiow159/Blog_app/assets/39752246/0aef7222-8095-4e00-a66a-7e419e694838)
![用戶管理頁面](https://github.com/yaiiow159/Blog_app/assets/39752246/a556b535-4ec5-449b-b64e-f35124ae4dc4)
![用戶管理更新成功](https://github.com/yaiiow159/Blog_app/assets/39752246/c22586ce-5eb0-4c9a-b224-c1f1bcaaafe9)
![用戶管理刪除成功](https://github.com/yaiiow159/Blog_app/assets/39752246/db28d968-0e45-47a2-8c0e-a0935794bd14)

### 群組管理畫面
![群組管理頁面](https://github.com/yaiiow159/Blog_app/assets/39752246/924defb5-bb32-477f-b287-56793a820968)
![Uploading 標籤刪除成功 - 複製.png…]()
![群組管理新增成功](https://github.com/yaiiow159/Blog_app/assets/39752246/71d53c16-3ab3-42f6-b457-3a18d615f33a)
![群組管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/8815148a-a036-40b1-ab2a-a73483bf59a8)

### 角色管理畫面
![角色管理編輯](https://github.com/yaiiow159/Blog_app/assets/39752246/66b242db-e76c-45b1-9ddf-9a896db85524)
![角色管理新增](https://github.com/yaiiow159/Blog_app/assets/39752246/5fa671f9-2ae4-4bdf-ac70-ea18ebf7edef)
![角色管理畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/53588166-e79b-40d2-a0ea-9af5387e7f8f)
![角色管理刪除](https://github.com/yaiiow159/Blog_app/assets/39752246/73a1f5de-0826-4785-b927-0b8f05ebf87b)


### 使用者登入紀錄畫面
![使用者登入紀錄畫面](https://github.com/yaiiow159/Blog_app/assets/39752246/e3477767-c5ca-471d-8877-820126fb150d)

### 郵件通知畫面
![郵件通知頁面](https://github.com/yaiiow159/Blog_app/assets/39752246/d591f89c-95ff-46ef-af55-5ef100e427cd)





