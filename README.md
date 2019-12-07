## 介绍

掌上大交小程序，提供教务信息，一卡通信息，图书查询，后勤报修，就业信息等服务。

（此处放图片）

## 前端

（此处放仓库地址）

感谢以下优秀小程序开源组件库，掌上大交小程序前端基于 `Color UI` 以及 `Vant Weapp` 组件库。

- [Color UI](https://github.com/weilanwl/ColorUI)
- [Vant Weapp](https://github.com/youzan/vant-weapp)

## 体验

扫描二维码

（此处放二维码）

## 开发细节

#### 一、技术栈及工具

- 开发框架：`SpringBoot`
- 数据库：`MySQL`
- 持久层框架：`MyBatis`
- 缓存中间件：`Redis`
- 爬虫框架：`HttpClient`，`Jsoup`
- 构建工具：`Maven`
- 日志框架：`Log4j`
- 效率工具：`Lombok`，`fastjson`，`Swagger2`，`tk.mybatis`，`MyBatisCodeHelper-Pro`

#### 二、爬虫的使用

在模拟教务登录，抓取新闻，后勤报修等功能开发中，必然要用到爬虫，在这里我选用的是 `Apache HttpClient` 发起请求，使用 `Jsoup` 来解析返回的 `HTML` 代码，并整理成 `Java Bean`，需要返回给前端的直接返回（比如新闻），需要存储的先存储再返回（比如成绩，没必要每次都去登录抓取，耗费性能与带宽且学校网站不是很稳定，可在前端引导用户重新登录以便更新，此逻辑以后版本会改变）。

##### 验证码与模拟登录如何保证统一为一个客户端

以登录教务为例，首先获取验证码，然后等待用户填入登录信息，再向教务网站发起登录，这对于教务网站来说是两次请求。

1. 小程序端用户没有登录教务，引导登录，此时后端请求验证码保存至本地下发至前端，但前端可能会因为验证码难以识别转而点击刷新验证码，这又是一次请求。
2. 结合 `Spring` 中 `Bean` 的 5 种作用域，默认是 `singleton` 的，也就是说，在小程序 - 后端 - 教务三者中，后端里起中间作用的那个 `Controller` 是单例的，验证码刷新后教务存储在后端里的 `Cookie` 便失效了，此时若用刷新后的验证码再结合用户信息由原有的 `Controller` 发起请求，登录会失败。

于是我们将登录教务的 `Controller` 的作用域改为 `session`，这样就保证了，就算前端刷新多少次验证码，前端提交的表单和验证码总是属于同一个作用域的。

```java
@Scope("session")
@RestController
@RequestMapping("/api/v1/jw")
public class JWController {
    // ...
}
```

但这样虽然在浏览器或 `Postman` 中测试通过，但又会引发下一个问题：

##### 小程序没有 Cookie 机制，如何统一联系两次请求

传统的浏览器是会在请求时携带 `Cookie`，以便让服务器识别这多次请求为同一个客户端。但小程序没有 `Cookie`，每次发起 `wx.request()`，是一次新的请求，就算有上面的改造，小程序发起请求获得验证码，待用户填入登录表单传给后端，后端是无法识别到底是哪个客户端发来的表单，同时 `HttpClient` 无法和从教务得来的 `Cookie` 结合起来，登录又会失败。

在这里我们在前端每次调用刷新验证码函数后，把后端发来的 `Cookie` 缓存到 storage 中，然后在发起登录请求时读取该 `Cookie`，加入到 `header` 里，便可以改造原有的 `wx.request()` 方法。

```javascript
  /**
   * 改变验证码
   */
  changeVerify() {
    var that = this;
    wx.request({
      url: config.jwVerifyUrl,
      data: {},
      header: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      success: res => {
        if (res.data.meta.status === 200) {
          // 保存 Cookie 到 Storage
          if (res && res.header && res.header['Set-Cookie']) {
            wx.setStorageSync('cookieKey', res.header['Set-Cookie']);
          }
          that.setData({
            verifyUrl: res.data.data
          })
        } else if (res.data.meta.status === 400) {}
      }
    })
  },
  /**
   * 登录教务
   */
  loginJW() {
    var that = this;
    // 发起登录
    wx.showLoading({
      title: '正在登录',
    })
    // 取出Cookie
    let cookie = wx.getStorageSync('cookieKey');
    let header = {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
    if (cookie) {
      header.Cookie = cookie;
    }
    wx.request({
      url: config.jwLoginUrl,
      data: {
        // ...
      },
      header,
      success: res => {
        // ...
      }
    })
  },
```

#### 三、缓存中间件 `Redis`

##### 使用缓存的必要

在抓取到学校新闻列表，新闻详情，这些一天或几天不会改变的数据，那么不必重新抓取了，否则每次用户请求都要重新发起抓取新闻获取详情，是不必要的开销。可对这些数据加以缓存（即使用 `fastjson` 将对象序列化为字符串），一旦有人访问过了某个新闻，在后面的访问中，不会再发起爬虫抓取，而是直接从 `Redis` 里读取（即使用 `fastjson` 将字符串反序列化为对象）并返回。由于 `Redis` 是内存数据库，因此速度非常快。

##### 过期时间

对于学校新闻列表、教务通知以及新闻详情的缓存，我设置了 24 小时的 `TTL`（也可以设置更长， 24 小时一是为了清理内存，二是新闻页有浏览量信息得更新）；而对于招聘会详情等比较重要的数据，则设置了 12 小时的过期时间。到期后会自动销毁。

#### 四、小程序登录态维护以及安全

##### [微信官方文档 - 登录流程时序](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html)

![](https://res.wx.qq.com/wxdoc/dist/assets/img/api-login.2fcc9f35.jpg)

可以看到使用微信账号体系的小程序的登录是有些繁琐的，这里我使用了 `Redis` 作为自定义登录态维护的存储。

##### 新用户

新用户打开小程序时，首先会检测本地的 `storage` 中是否有 `sessionId`，接着会携带此 `sessionId`（此时实际上不存在）请求后端检查登录态，后端在 `Redis` 里根据此 `sessionId` 获取 `value` 为 `null`，告诉前端为未登录态，此时仍然不知道该用户是新用户。

前端得知未登录后，便会发起请求至登录接口，首先请求 `wx.login()` 微信服务器的接口，取得 `code`，此 `code` 只能使用一次，`code` 获取成功后在成功回调函数里发起请求登录，后端接收到请求后，便会携带 `code` 使用 `HttpClient` 向微信服务器发起登陆凭证校验，成功返回得到换取的 `sessionKey` 以及 `openid`，然后根据 `openid` 去数据库查询该用户是否是老用户，如果是则更新该用户的 `sessionKey`，如果是新用户则保存该用户到数据库，最终都会返回 `sessionId`。

##### 老用户

老用户在打开小程序后会有两种可能：

1. 24 小时内使用过小程序

   由于 `Redis` 里设置自定义登录态为 24 小时，所以此时 `sessionId` 仍然有效，从本地的 `storage` 获取到 `sessionId` 后，请求后端，然后会从 `Redis` 里拿到该用户的 `openid`，查询 `MySQL` 发现用户存在，返回前端告知登录成功，显示用户信息。

2. 距离上次使用超过 24 小时

   超过 24 小时后，`sessionId` 会失效，便会发起 `wx.login()` 接口，获取新的 `code`，然后后端又会拿到新的 `sessionKey` 以及 `openid`，根据 `openid` 去数据库查询发现用户存在，则更新 `sessionKey` 并返回 新的 `sessionId`。

也就是说 `wx.login()` 至多每天请求一次，当然也可每次打开都请求该接口，使用 `sessionKey` 来维护登录态，但没有自定义登录态用的舒服。

##### 访问业务接口的关键：`sessionId`（也可称为 `token`）

要注意的是小程序没有 `Cookie`，因此无法按照常规 `BS` 设计使用 `Cookie` 里的 `Session` 来辨别用户。

那如何鉴别每个用户呢，微信官方文档提示：

> 会话密钥 `session_key` 是对用户数据进行 [加密签名](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html) 的密钥。为了应用自身的数据安全，开发者服务器**不应该把会话密钥下发到小程序，也不应该对外提供这个密钥**。

那么干脆就把 `openid` 也不在网络中传输了，而是用随机字符串 `UUID` 作为每个用户的唯一标识符，使用 `UUID` 作为 `sessionId` 的 `key`，使用 `sessionKey + openid` 组合来作为 `value`，保存至 `Redis` 里即可。

在关键业务接口中，前端请求中仅仅携带 `sessionId` 即可，虽然 `UUID` 是随机无重复并且是 24 小时过期的，为了潜在的被盗取的可能，可以绑定设备信息来再次鉴别是否是真的用户。

## 开源协议

本项目基于 [MIT](https://zh.wikipedia.org/wiki/MIT許可證) 协议，请自由地享受和参与开源。

## 更新日志

##### 2019.12.05 

完成所有功能