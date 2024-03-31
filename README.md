# 部落客系統
--------------------------------------------------------------------------------------------------------------------
系統介紹：使用springboot + vue 的前後端分離項目，可以根據各角色(管理員、一般使用者等)來控制顯示頁面選單
採用jwt-token來控制登入時間以及權限控制，系統功能包括分類管理、文章管理、使用者管理、使用者群組管理、用戶明細，瀏覽紀錄等功能

系統流層說明
前端使用axios來傳遞前端資料至後端，後端使用dto接收資料，並使用mapstructer來轉換dto至po物件並在dao層進行crud操作<br>
系統可以針對容器化配置以及本地化配置有分別使用不同配置檔進行設定

資料庫規劃:
![Diagram 1](https://github.com/yaiiow159/Blog_app/assets/39752246/d73d894b-28b6-4dd0-9bc1-437d94935b34)

---------------------------------------------------------------------------------------------------------------------
前端使用技術: vue3 + pinia(全局狀態管理) + tainwindcss(css樣式) + axios(前後端資料傳遞) <br>
後端使用技術: springboot3 (spring配置框架) + spring-security(權限控制) + spring-data-jpa(dao層操作) <br>
消息隊列: kafka + zookeeper <br>
資料庫: mysql8 <br>
非關係型資料庫: redis <br>
測試使用工具: swagger3 (open-api)、postman <br>
容器化: docker、docker-compose <br>

