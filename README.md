# 部落客系統
這個項目是一個簡單的部落格系統，可以進行張貼文章以及撰寫評論回復留言等基本功能，如果是管理員身分的話可額外使用使用者管理、群組管理、權限管理等功能設置系統 <br>
另外頁面上參照如dcard的一些頁面設計 可以在使用者資料上查看當前張貼文章數、評論數、按讚數等訊息，系統也能進行全文搜尋 利用文章內文 標題 作者等進行查詢 <br>
另外再增加一些我個人想法增添一些額外的功能

<hr>

## 系統介紹：
使用springboot + vue 的前後端分離項目，使用spring-security進行功能的權限驗證管理、因項目是採取前後分離因此設定可採不同源訪問 <br>
前端採用vite搭建vue3項目 搭配vuetify進行前端頁面撰寫、使用pinia、router 進行路由管理以及使用者資料全局狀態管理 <br>
後端使用springboot搭建spring項目、使用restful建置controller功能項，並且統一管理錯誤處理、回應物件處理等增加統一性 <br>
搭配docker、dockerfile、docker-compos 進行容器化處理 以及maven-plugin 進行docker-build push等處理 上傳項目至docker-hub <br>

目前正在進行: 撰寫前端後端的CI/CD流程、部屬至AWS-EC2上 <br>

## 系統流程說明 (構思說明)
前端使用axios來傳遞前端資料至後端，後端使用dto接收資料，並使用mapstructer來轉換dto至po物件並在dao層進行crud操作<br>
系統可以針對容器化配置以及本地化配置有分別使用不同配置檔進行設定

![blog_app系統流程圖 drawio](https://github.com/yaiiow159/Blog_app/assets/39752246/1396dde9-fd7b-4541-b3d4-f99ef3df579f)

資料庫規劃:
![Diagram 1](https://github.com/yaiiow159/Blog_app/assets/39752246/d73d894b-28b6-4dd0-9bc1-437d94935b34)

---------------------------------------------------------------------------------------------------------------------
## 使用技術介紹:
<hr>

前端使用技術: vue3 + pinia(全局狀態管理) + tainwindcss(css樣式) + axios(前後端資料傳遞) <br>
後端使用技術: springboot3 (spring配置框架) + spring-security(權限控制) + spring-data-jpa(dao層操作) <br>
消息隊列: kafka + zookeeper <br>
資料庫:mysql <br>
非關係型資料庫:redis <br>
文檔生成以及測試: openApi
容器化: docker、docker-compose <br>
雲服務技術: AWS(s3、ec2)

