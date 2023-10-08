# Blog_app
一個簡單的bkog_app後端應用程式 可以使用swagger3 搭配openAPI做認證測試
後端使用到的技術:
1. Springboot3
2. Spring-Security6
3. DataBase:Mysql8
4. IDE: Intellij
5. testing: Mockito，postman(API測試)、Swagger3
6. Spring-Data-JPA
7. MapStrct (Dto tranfermate to Entity) 映射使用
8. ExceptionHandler(統一常出現的錯誤做管理)
9. AOP應用 (使用AOP在創建評論或式文章時，過濾包含惡意留言的文章或是憑倫) 待做
10. Redis 主要作用式存放jwtToken，設置expireTime 做驗證
11. 基本CRUD操作
