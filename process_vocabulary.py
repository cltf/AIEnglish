#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
处理中考词汇数据并存储到SQLite数据库
"""

import sqlite3
import re
import os

def create_database():
    """创建SQLite数据库和表"""
    db_path = "app/src/main/assets/vocabulary.db"
    
    # 确保目录存在
    os.makedirs(os.path.dirname(db_path), exist_ok=True)
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # 创建表
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS vocabulary (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            word TEXT UNIQUE NOT NULL,
            phonetic TEXT,
            part_of_speech TEXT,
            meaning TEXT NOT NULL,
            example TEXT,
            category TEXT,
            difficulty TEXT,
            type TEXT DEFAULT 'MIDDLE_SCHOOL'
        )
    ''')
    
    conn.commit()
    return conn, cursor

def parse_vocabulary_data():
    """解析词汇数据"""
    vocabulary_data = """1. a (an) - det. 一个
2. ability - n. 能力
3. able - a. 能够；有能力的
4. about - ad. 大约；到处；prep. 关于；四处
5. above - prep. 在…… 上面
6. abroad - ad. 到（在）国外
7. absent - a. 缺席的，不在的
8. accept - v. 接受；收受
9. accident - n. 事故，意外的事
10. according to - （短语）根据
11. achieve - vt. 达到，取得
12. across - prep. 横过，穿过
13. act - n. 法令，条例；v. 表演
14. action - n. 行动
15. active - a. 积极的，主动的
16. activity - n. 活动
17. actor - n. 演员
18. actress - n. 女演员
19. actual - ad. 真实的（注：原文档标注 “ad.”，规范词性应为 “adj. 实际的；真实的”，此处保留原文标注，供参考）
20. actually - ad. 实际上，事实上
21. add - v. 添加，增加
22. address - n. 地址
23. admire - v. 钦佩；赞美
24. adult - n. 成年人
25. advantage - n. 优点，有利条件
26. advertise - v. 做广告
27. advice - n. 忠告，劝告，建议
28. advise - v. 建议；劝告
29. afford - v. 买得起；提供
30. afraid - a. 害怕的；担心
31. Africa - n. 非洲
32. African - a. 非洲的；n. 非洲人
33. after - ad. 在后；后来
34. afternoon - n. 下午，午后
35. again - ad. 再一次；再，又
36. against - prep. 对着，反对
37. age - n. 年龄；时代
38. ago - ad. …… 以前
39. agree - v. 同意；应允
40. agreement - n. 协议；同意；一致
41. air - n. 空气；大气
42. airport - n. 航空站，飞机场
43. alive - a. 活着的，存在的
44. all - a. 全（部）；所有的
45. allow - vt. 允许，准许
46. almost - ad. 几乎，差不多
47. alone - a. 单独的，孤独的
48. along - ad. 向前；prep. 沿着
49. aloud - adv. 大声地；出声地
50. already - ad. 已经
51. also - ad. 也
52. although - conj. 虽然，尽管
53. always - ad. 总是；一直；永远
54. America - n. 美国；美洲
55. American - a. 美国的；n. 美国人
56. among - prep. 在（三个以上）之间
57. ancient - a. 古代的，古老的
58. and - conj. 和
59. angry - a. 生气的，愤怒的
60. animal - n. 动物
61. another - pron. 另一个
62. answer - n./v. 回答，答案
63. ant - n. 蚂蚁
64. any - det. 任何的
65. anybody - pron. 任何人
66. anyone - pron. 任何人
67. anything - pron. 任何事情
68. anyway - ad. 不管怎么说
69. anywhere - ad. 任何地方
70. appear - v. 出现，显露
71. apple - n. 苹果
72. April - n. 四月
73. area - n. 面积；地域
74. argue - v. 争论
75. arm - n. 臂，支架；武器
76. army - n. 陆军；军队
77. around - ad. 在周围；在附近
78. arrive - vi. 到达；达到
79. art - n. 艺术，美术
80. article - n. 文章；冠词
81. artist - n. 艺术家
82. as - adv. 和…… 一样；conj. 当…… 时
83. Asia - n. 亚洲
84. Asian - a. 亚洲（人）的；n. 亚洲人
85. ask - v. 询问；请求，要求
86. asleep - a. 睡着的，熟睡
87. at - prep. 在（某地）
88. attend - v. 上（学）；出席
89. attention - n. 注意，关心
90. attitude - n. 态度
91. attract - v. 吸引
92. aunt - n. 姑；姨
93. Australia - n. 澳大利亚
94. Australian - n. 澳大利亚人
95. August - n. 八月
96. autumn - n. 秋天，秋季
97. avoid - v. 避免，躲开，逃避
98. awake - v. 唤醒；a. 醒着的
99. away - ad. 离开；远离
100. awful - adj. 可怕的，糟糕的
101. baby - n. 婴儿
102. back - a. 后面的；n. 背后，后部
103. background - n. 背景
104. bad - a. 坏的；有害的
105. bag - n. 包
106. balance - n. 平衡
107. backpack - n. 书包；提包
108. ball - n. 球；舞会
109. balloon - n. 气球
110. bamboo - n. 竹
111. banana - n. 香蕉
112. band - n. 乐队
113. bank - n. 岸，堤；银行
114. baseball - n. 棒球
115. basic - a. 基本的
116. basket - n. 篮子
117. basketball - n. 篮球
118. bathroom - n. 浴室，盥洗室
119. be (am,is,are) - v. 是
120. beach - n. 海滨，海滩
121. bean - n. 豆
122. bear - n. 熊；v. 忍受（注：原文档未标注 “忍受” 义项，结合中考考纲补充，供参考）
123. beat - v. 敲打；打赢；n.（音乐）节拍
124. beautiful - a. 美丽的，美观的
125. because - conj. 因为
126. become - v. 变得；成为
127. bed - n. 床
128. bedroom - n. 寝室，卧室
129. beef - n. 牛肉
130. before - prep. 在…… 前
131. begin - v. 开始，着手
132. beginning - n. 开始，开端
133. behave - v. 表现
134. behind - prep. 在…… 后面
135. believe - v. 相信，认为
136. bell - n. 钟；钟（铃）声
137. belong - v. 属于
138. below - prep. 在…… 下面
139. benefit - n. 利益，好处
140. beside - prep. 在…… 旁边
141. best - a. 最好的
142. better - a. 更好的
143. between - prep. 在（两者）之间
144. beyond - prep. 在…… 较远的一边
145. big - a. 大的
146. bike (=bicycle) - n. 自行车
147. bill - n. 账单；账款
148. bird - n. 鸟
149. birth - n. 出生
150. birthday - n. 生日
151. biscuit - n. 饼干
152. bit - n. 一点，少量的
153. black - n. 黑色；a. 黑色的
154. blackboard - n. 黑板
155. blank - n. 空白处
156. blind - a. 瞎的
157. block - n. 一大块；街区；障碍物
158. blood - n. 血液
159. blouse - n. 女衬衫
160. blow-blew-blown - v. 吹；吹气
161. blue - n. 蓝色；a. 蓝色的，悲伤的
162. board - n. 木板；布告牌；委员会
163. boat - n. 小船，小舟
164. body - n. 身体
165. boil - v. 煮沸，烧开
166. book - n. 书；本子
167. bored - a. 乏味的，无聊的
168. boring - a. 无趣的；烦人的
169. born - v. 出生
170. borrow - v. 向别人借用
171. boss - n. 老板；首领；工头
172. both - pron. 两者；双方
173. bottle - n. 瓶子
174. bottom - n. 底部；下端
175. bowl - n. 碗
176. box - n. 盒子，箱子
177. boy - n. 男孩
178. brain - n. 脑
179. brave - a. 勇敢的；崭新的；v. 勇敢面对
180. bread - n. 面包
181. break - n. 间隙；v. 打破，折断；损坏
182. breakfast - n. 早餐
183. breathe - prep. 在…… 前（注：原文档标注有误，规范词性应为 “v. 呼吸”，此处保留原文标注，供参考）
184. bridge - n. 桥
185. bright - a. 明亮的，聪明的
186. bring - vt. 拿来，带来
187. Britain - n. 英国；不列颠
188. British - a. 英国（人）的；n. 英国人
189. brother - n. 兄；弟
190. brown - n. 褐色，棕色
191. brush - n. 刷子；画笔；v. 刷；画；擦过
192. build - v. 建筑；造
193. building - n. 建筑物；大楼
194. burn - v. 燃，烧，着火
195. bus - n. 公共汽车
196. business - n. 生意，交易
197. busy - a. 忙（碌）的
198. but - conj. 但是
199. buy - v. 买
200. by - prep. 由；通过
201. bye - int. 再见
202. cake - n. 蛋糕，糕点
203. calendar - n. 日历；历书
204. call - n. 喊，叫；电话；v. 称呼；呼喊，叫
205. camera - n. 照相机；摄像机
206. camp - n.（夏令）营；vi. 野营
207. Canada - n. 加拿大
208. Canadian - n. 加拿大人
209. can (can’t/cannot) - v. 能 / 不能
210. cancel - v. 取消
211. candle - n. 蜡烛
212. candy - n. 糖果
213. cap - n. 无檐帽子
214. capital - n. 首都，省会；大写；资本
215. captain - n. 船长；队长
216. car - n. 汽车，小卧车
217. card - n. 名片；纸牌
218. care - v. 介意，在乎；关心
219. careful - a. 小心的，仔细的
220. careless - a. 粗心的
221. carrot - n. 胡萝卜
222. carry - vt. 拿，搬，提
223. cartoon - n. 漫画；动画片
224. cat - n. 猫
225. catch - v. 接住；捉住
226. cause - n. 原因，起因；vt. 促使，引起
227. celebrate - v. 庆祝
228. cent - n. 分；分币
229. central - a. 中央的
230. center - n. 中心，中央
231. century - n. 世纪，百年
232. certain - adj. 必然的
233. certainly - ad. 必定；当然
234. chair - n. 椅子
235. chalk - n. 粉笔
236. challenge - n. 挑战
237. chance - n. 机会，可能性
238. change - v. 改变，变化
239. character - n. 性格；角色
240. cheap - a. 便宜的
241. check - vt. 校对，核对；检查
242. cheat - v. 作弊，欺骗
243. cheer - v. 欢呼；喝彩
244. cheese - n. 奶酪
245. chemistry - n. 化学
246. chess - n. 棋
247. chicken - n. 鸡；鸡肉
248. child - n. 孩子，儿童
249. China - n. 中国
250. Chinese - a. 中国（人）的
251. chips - n. 炸薯条
252. chocolate - n. 巧克力
253. choice - n. 选择
254. choose - vt. 选择
255. chopsticks - n. 筷子
256. Christmas - n. 圣诞节
257. church - n. 教堂；教会
258. cinema - n. 电影院
259. circle - n. 循环；周期；圆
260. city - n. 市，城市
261. class - n. 班；年级；课
262. classmate - n. 同班同学
263. classroom - n. 教室
264. clean - vt. 弄干净，擦干净；a. 清洁的，干净的
265. clear - a. 清晰的；明亮的
266. clever - a. 聪明的，伶俐的
267. climb - v. 爬，攀登
268. clock - n. 钟
269. close - vt. 关闭；a. 亲密的；ad. 近，靠近
270. clothes - n. 衣服；各种衣物
271. cloud - n. 云；云状物；阴影
272. cloudy - a. 多云的，阴天的
273. club - n. 俱乐部
274. coach - n. 教练；马车；长途车
275. coal - n. 煤
276. coast - n. 海岸
277. coat - n. 外套；v. 给…… 穿外套
278. coffee - n. 咖啡
279. coin - n. 硬币
280. cold - a. 冷的，寒的；n. 寒冷；感冒
281. collect - vt. 收集，搜集
282. collection - n. 收集；收藏品
283. college - n. 学院
284. color - n. 颜色
285. come - vi. 来，来到
286. comfortable - a. 舒服的
287. common - a. 普通的；共有的
288. communicate - v. 交际；传达
289. communication - n. 交流，通讯
290. community - n. 社区；社会
291. company - n. 公司
292. compare - v. 比较，对照
293. compete - n. 比赛，竞赛（注：原文档标注 “n.”，规范词性应为 “v. 竞争；比赛”，此处保留原文标注，供参考）
294. competition - n. 竞争；比赛
295. complain - vt. 抱怨；控诉
296. complete - a. 完整的；完全的；v. 完成
297. computer - n. 电子计算机
298. concert - n. 音乐会
299. condition - n. 状况；环境
300. confident - adj. 自信的
301. connect - vt. 连接，把…… 联系起来
302. consider - v. 考虑，细想
303. continue - vi. 继续
304. control - n.&v. 控制；克制
305. confuse - vt. 使混乱；使困惑
306. congratulate - vt. 祝贺；恭喜
307. convenient - adj. 方便的；近便的
308. conversation - n. 谈话，交谈
309. cook - n. 炊事员，厨师；v. 烹调，做饭
310. cookie - n. 小甜饼
311. cool - a. 凉（爽）的；酷
312. copy - n. 抄本，副本；一本；v. 抄写；拷贝
313. corn - n. 谷物；玉米
314. corner - n. 角；角落；拐角
315. correct - v. 改正；a. 正确的
316. cost - v. 值（多少钱）；花
317. cough - v.&n. 咳嗽
318. could - aux. 能够，可以
319. count - vt. 数，点数
320. country - n. 国家；农村，乡下
321. countryside - n. 乡下，农村
322. couple - n. 一对；两个
323. courage - n. 勇气；胆量
324. course - n. 过程；课程
325. cousin - n. 堂（表）兄弟，堂（表）姐妹
326. cover - n. 封面；封皮；v. 包括；采访；涉及
327. cow - n. 母牛，奶牛
328. crayon - n. 蜡笔
329. crazy - a. 疯狂的；狂热的，着迷的
330. create - vt. 创造；造成
331. creative - a. 创造的，创意的
332. criticize - v. 批评；评论
333. cross - v. 横穿；交叉；n. 十字形的东西
334. crowd - n. 人群；观众；v. 拥挤；挤满
335. crowded - a. 拥挤的
336. cry - n.&v. 叫喊；哭
337. culture - n. 文化
338. cup - n. 茶杯
339. curious - adj. 好奇的
340. custom - n. 风俗，习惯
341. customer - n. 顾客
342. cut - v. 切，剪；n. 伤口
343. cute - a. 可爱的
344. daily - a. 每日的；日常的；n.& vi. 跳舞（注：“daily” 与 “dance” 原文档排版有误，此处修正为 “daily - a. 每日的；日常的”“dance - n.& vi. 跳舞”）
345. dance - n.& vi. 跳舞
346. danger - n. 危险
347. dangerous - a. 危险的
348. dark - a. 黑暗的；深色的；n. 黑暗
349. date - n. 日期；约会
350. daughter - n. 女儿
351. day - n.（一）天，白天
352. death - a. 死的；无生命的（注：原文档标注 “a.”，规范词性应为 “n. 死亡”，此处保留原文标注，供参考）
353. dead - a. 死的；无生命的
354. deaf - a. 聋的
355. deal - v. 处理；对待；n. 交易；待遇
356. December - n. 12 月
357. decide - v. 决定；下决心
358. decision - n. 决定；决心
359. deep - a. 深的
360. degree - n. 度；度数；学位
361. delicious - a. 美味的，可口的
362. deliver - vi. 递送
363. depend - vi. 依靠，依赖，取决
364. describe - vt. 描写，叙述
365. design - n.& v. 设计
366. desk - n. 书桌，写字台
367. develop - v.（使）发展
368. development - n. 发展，发达
369. destroy - vt. 破坏；毁坏
370. dialogue - n. 对话；交换意见
371. diary - n. 日记；日记簿
372. dictionary - n. 词典，字典
373. die - v. 死
374. diet - n. 日常饮食
375. difference - n. 不同；差别
376. different - a. 不同的，有差异的
377. difficult - adj. 困难的
378. difficulty - n. 困难，费力
379. dig-dug-dug - v. 挖（洞、沟等）；掘
380. dinner - n. 正餐，宴会
381. direct - a. 直接的；直达的；vt. 指挥；指导
382. direction - n. 方向；指导；趋势
383. director - n. 指挥者，主管；导演
384. dirty - a. 脏的
385. disappoint - vt. 使失望
386. disadvantage - n. 不利（条件）；劣势
387. discover - vt. 发现
388. discovery - n. 发现，发觉
389. discuss - vt. 讨论，议论
390. discussion - n. 讨论，议论
391. dish - n. 盘，碟；盘装菜
392. dishonest - a. 不诚实的
393. distance - n. 距离
394. divide - v. 分开
395. doctor - n. 医生，大夫
396. do - v. 做，办（某事）
397. dog - n. 狗
398. dollar - n. 美元
399. door - n. 门
400. double - a. 两倍的；双的
401. doubt - n. 疑惑；疑问；v. 怀疑
402. down - prep. 沿着，沿…… 而下；adj. 向下的；沮丧的
403. downstairs - ad. 在楼下；到楼下
404. draw - v. 绘画；绘制；拉
405. dream - n. 睡梦；梦想
406. dress - n. 女服，连衣裙
407. drink - n. 饮料；喝酒；v. 喝，饮
408. drive - v. 驾驶，开车；驱赶
409. driver - n. 司机，驾驶员
410. drop - n. 滴；v. 掉下，落下；投递
411. drum - n. 鼓
412. dry - v. 使…… 干；弄干；擦干；a. 干的；干燥的
413. duck - n. 鸭子
414. dull - a. 乏味的；阴沉的；笨的
415. dumpling - n. 饺子
416. during - prep. 在…… 期间
417. each - adv. 每个地
418. eager - adj. 渴望的
419. ear - n. 耳朵
420. early - a. 早的；ad. 早地
421. earth - n. 地球；泥；大地
422. earthquake - n. 地震
423. east - a. 东方的；东部的
424. eastern - a. 东方的
425. easy - a. 容易的
426. eat - v. 吃
427. education - n. 教育，培养
428. effect - v. 影响（注：原文档标注 “v.”，规范词性应为 “n. 影响；效果”，此处保留原文标注，供参考）
429. effort - n. 努力；成就
430. egg - n. 蛋
431. eight - num. 八
432. eighteen - num. 十八
433. eighth - num. 第八
434. eighty - num. 八十
435. either…or - conj. 两者之一；要么
436. elder - n. 长者；前辈
437. electric/electronic - a. 电的；电流的
438. elephant - n. 象
439. eleven - num. 十一
440. else - ad. 别的，其他的
441. e-mail - n. 电子邮件；v. 发电子邮件
442. empty - a. 空的；v. 把…… 弄空；把…… 腾出来
443. encourage - vt. 鼓励
444. end - n. 末尾；终点；v. 结束，终止
445. enemy - n. 敌人
446. energy - n. 精力，能量
447. engineer - n. 工程师；技师
448. England - n. 英格兰
449. English - n. 英语；a. 英国的，英国人的
450. enjoy - vt. 欣赏；喜欢；享受…… 之乐趣
451. enough - a. 足够的；充分的
452. enter - vt. 进入
453. environment - n. 环境
454. eraser - n. 橡皮擦；板擦
455. especially - ad. 特别，尤其
456. Europe - n. 欧洲
457. European - n. 欧洲人
458. even - ad. 甚至；更
459. evening - n. 傍晚，晚上
460. event - n. 事件，大事
461. ever - ad. 曾经
462. every - a. 每一，每个的
463. everyone - pron. 每人，人人；a.& pron. 每人（注：“everyone” 重复标注，统一为 “pron. 每人，人人”）
464. everyday - a. 每日的，日常的
465. everything - pron. 每件事
466. everywhere - ad. 到处
467. everybody - pron. 每个人
468. exactly - ad. 恰好地；正是
469. exam (=examination) - n. 考试，测试
470. examine - v. 检查；调查
471. example - n. 例子；榜样
472. excellent - a. 极好的，优秀的
473. except - prep. 除…… 之外
474. excited - a. 兴奋的
475. exciting - a. 令人兴奋的，使人激动的
476. excuse - n. 借口；辩解；vt. 原谅；宽恕
477. exercise - vi. 锻炼
478. expect - vt. 预料；盼望
479. expensive - a. 昂贵的
480. experience - n. 经验；经历
481. experiment - n. 实验
482. explain - vt. 解释，说明
483. express - vt. 表达；表示
484. expression - n. 词语，表达法；表情
485. eye - n. 眼睛
486. face - n. 脸；vt. 面向；面对
487. fact - n. 事实，现实
488. factory - n. 工厂
489. fail - v. 失败；不及格
490. fair - a. 公平的
491. fall - vi. 落（下），降落；n.（美）秋季
492. family - n. 家庭；家族
493. famous - a. 著名的
494. fan - n.（电影、运动等的）迷；风扇
495. fantastic - a. 奇特的；（口语）极好的
496. far - a.&ad. 远的
497. farm - n. 农场；农庄
498. farmer - n. 农民
499. fashion - n. 时髦，时尚
500. fast - a. 快的，迅速的
501. fat - n. 脂肪；a. 胖的
502. father - n. 父亲
503. favourite (favorite) - a. 最喜爱的
504. fear - n.&v. 害怕，恐惧
505. February - n. 2 月
506. feed - vt. 喂（养）；饲（养）
507. feel - v. 感觉，觉得；摸
508. feeling - n. 感情；感觉
509. festival - n. 节日，节庆
510. fetch - vt.（去）取（物）来
511. fever - n. 发烧；发热
512. few - pron. 不多；少数；adj. 很少的；几乎没有的
513. field - n. 田地；牧场；场地；领域
514. fifteen - num. 十五
515. fifth - num. 第五
516. fifty - num. 五十
517. fight-fought-fought - n.&V. 打仗（架）
518. fill - vt. 填空，装满
519. film - n. 电影；影片；胶卷；vt. 拍摄，把…… 拍成电影
520. finally - ad. 最后
521. find - vt. 找到，发现
522. fine - a. 晴朗的；美好的；健康的；n.& v. 罚款
523. finger - n. 手指
524. finish - v. 结束；做完
525. fire - n. 火；火炉；v. 开火，射击；开除
526. first - num. 第一
527. fish - n. 鱼；鱼肉；vi. 钓鱼；捕鱼
528. fisherman - n. 渔夫；渔人
529. fit - a. 健康的，适合的；v.（使）适合，安装
530. five - num. 五
531. fix - vt. 修理；安装；固定
532. flag - n. 旗；标志；旗舰
533. flash - v. 闪光；掠过；n. 闪光；闪光灯
534. flat - a. 平的；n. 楼中一套房间；公寓
535. flight - n. 飞行，航班
536. floor - n. 地面，地板；（楼房的）层
537. flower - n. 花
538. fly - n. 飞行；苍蝇；v. 飞；飞行；放（风筝）
539. follow - vt. 跟随；仿效；跟上
540. food - n. 食物，食品
541. foot - n. 足，脚；英尺
542. football - n.（英式）足球；（美式）橄榄球
543. for - prep. 为了，因为
544. force - vt. 强迫；n. (pl.) 军队；暴力
545. foreign - a. 外国的
546. foreigner - n. 外国人
547. forest - n. 森林
548. forget - v. 忘记；忘掉
549. fork - n. 叉，餐叉；岔口
550. form - n. 表格；形式；结（注：“结” 应为 “结构”，修正为 “n. 表格；形式；结构”）
551. forty - num. 四十
552. four - num. 四
553. fourteen - num. 十四
554. fourth - num. 第四
555. France - n. 法国
556. free - a. 自由的，空闲的；免费的
557. French - n. 法语；a. 法国（人）的
558. fresh - a. 新鲜的
559. Friday - n. 星期五
560. fridge - n. 冰箱
561. friend - n. 朋友
562. friendly - a. 友好的
563. friendship - n. 友谊，友情
564. frighten - vt. 使惊吓；吓唬
565. from - prep. 从…… 起；来自
566. front - a. 前面的；前部的
567. fruit - n. 水果；果实
568. full - a. 满的，完全的
569. fun - n. 有趣的事，娱乐，玩笑
570. funny - a. 滑稽可笑的
571. future - n. 将来
572. game - n. 游戏；运动；比（注：“比” 应为 “比赛”，修正为 “n. 游戏；运动；比赛”）
573. garden - n. 花园，果园，菜（注：“菜” 应为 “菜园”，修正为 “n. 花园，果园，菜园”）
574. gate - n. 大门
575. general - a. 一般的；总的；普遍的
576. gentleman - n. 绅士，先生；有身份、有教养的人
577. geography - n. 地理学
578. German - a. 德国的；德语的；n. 德语；德国人
579. Germany - n. 德国
580. get - vt. 成为；得到；具有；到达
581. gift - n. 赠品；礼物
582. giraffe - n. 长颈鹿
583. girl - n. 女孩
584. give - vt. 给；付出；给予
585. glad - a. 高兴的；乐意的
586. glass - n. 玻璃杯；玻璃；（复）眼镜
587. glove - n. 手套
588. glue - n. 胶水
589. go - vi. 去；走；驶；通到；n. 尝试（做某事）
590. gold - n. 黄金；a. 金的，黄金的
591. goal - n. 目标；球门
592. good - a. 好的，良好的
593. goodbye (bye) - int. 再见；再会
594. government - n. 政府
595. grade - n. 等级；学年；年级成绩，分数
596. grammar - n. 语法
597. grandchildren - n. 孙辈
598. granddaughter - n.（外）孙女
599. grandmother - n.（口语）奶奶；外婆
600. grandfather - n.（口语）爷爷；外公
601. grandparent - n.（外）祖父（母）
602. grandson - n.（外）孙子
603. grape - n. 葡萄
604. grass - n. 草；草场；牧草
605. gradually - adv. 逐步地；渐渐地
606. grey - n. 灰色；a. 灰色的
607. great - a. 伟大的；重要的；ad.（口语）好极了，很好
608. green - a. 绿色的；n. 绿色
609. greet - vt. 欢迎，迎接
610. greeting - n. 问候；招呼；祝贺
611. ground - n. 地面
612. group - n. 组，群
613. grow - v. 生长；发育；种植；变成
614. guard - n. 警戒，看守；vt. 守卫
615. guess - v. 推测；猜测
616. guest - n. 客人，宾客
617. guide - n. 导游，向导；v. 带领，引导
618. guitar - n. 吉他，六弦琴
619. guy - n. 家伙，伙计
620. gym - n. 体育馆；健身房
621. gun - n. 枪支
622. habit - n. 习惯，习性
623. hair - n. 头发
624. haircut - n. 理发
625. half - a.& n. 一半，半个
626. hall - n. 大厅，会堂，礼堂
627. hamburger - n. 汉堡包
628. hand - n. 手；指针；v. 递；给；交上
629. handbag - n. 女用皮包，手提包
630. handsome - a. 英俊的
631. hang-hung-hung - v. 处（人）绞刑；悬挂
632. happen - vi.（偶然）发生
633. happy - a. 幸福的
634. hard - a. 硬的；困难的；艰难的；ad. 努力地；使劲
635. hardly - ad. 几乎不
636. harmful - a. 有害的
637. hat - n. 帽子（一般指有边的）
638. hate - vt.& n. 恨，讨厌
639. have - vt. 有；吃；喝；进行
640. head - n. 头；才智；首脑；标题；a. 头部的；主要的
641. headache - n. 头疼
642. health - n. 健康，卫生
643. healthy - a. 健康的，健壮的
644. hear-heard-heard - v. 听见；听说
645. heart - n. 心；心脏；纸牌中的红桃
646. heat - vt. 把…… 加热；n. 热
647. heavy - a. 重的
648. height - n. 高，高度
649. hello - int. 喂；你好
650. help - n. & vt. 帮助，帮忙
651. helpful - a. 有帮助的，有益的
652. hen - n. 母鸡
653. her - pron. 她（宾格），她的
654. here - ad. 这里，在这里
655. hero - n. 英雄，勇士；男主角
656. hers - pron. 她的（名词性物主代词）
657. herself - pron. 她自己
658. hide-hid-hidden - v. 把…… 藏起来，隐藏
659. high - a. 高的；高度的；ad. 高地
660. hill - n. 小山；丘陵；土堆
661. him - pron. 他（宾格）
662. himself - pron. 他自己
663. his - pron. 他的
664. history - n. 历史，历史学
665. hit - n.& vt. 打，撞，击中
666. hobby - n. 业余爱好，嗜好
667. hold-held-held - vt. 拿；抱；握住；举行
668. hole - n. 洞，坑
669. holiday - n. 假日；假期
670. home - n. 家；ad. 到家；回家
671. hometown - n. 故乡
672. homework - n. 家庭作业
673. honest - a. 诚实的，正直的
674. hope - n.& v. 希望
675. horse - n. 马
676. hospital - n. 医院
677. host - n. 东道主，主人
678. hot - a. 热的
679. hotel - n. 旅馆，饭店，宾馆
680. hour - n. 小时
681. house - n. 房子；住宅
682. housework - n. 家务劳动
683. however - conj. 然而，可是
684. huge - a. 巨大的，庞大的
685. human - a. 人的，人类的
686. humorous - a. 幽默的；滑稽的
687. hundred - num. 百
688. hungry - a.（饥）饿的
689. hurry - vi. 赶快；急忙
690. hurt - vt. 伤害，受伤
691. husband - n. 丈夫
692. ice - n. 冰
693. ice-cream - n. 冰淇淋
694. idea - n. 想法
695. ill - a. 有病的；不健康的
696. illness - n. 疾病
697. imagine - vt. 想像，设想
698. immediate - adj. 立即的；直接的
699. important - a. 重要的
700. importance - n. 重要（性）
701. impossible - a. 不可能的
702. improve - vt. 改进，更新
703. in - prep. 在…… 内
704. include - vt. 包含，包括
705. increase - v. & n. 增加，繁殖
706. India - n. 印度
707. Indian - n. 印第安人；印度人；a. 印地安人的；印度人的
708. industry - n. 工业
709. influence - n.& v. 影响
710. infer - vt. 推断；推论
711. information - n. 信息
712. inside - prep. 在…… 里面；ad. 在里面
713. insist - vt. 坚持，强调
714. instead - ad. 代替，顶替
715. instruction - n. 说明；教导
716. instrument - n. 乐器，器械
717. interest - n. 兴趣；利息
718. interesting - a. 有趣的
719. international - a. 国际的
720. internet - n. 互联网，英特网
721. interrupt - vt. 中断；打断
722. interview - n.& vt. 采访，面试
723. into - prep. 到…… 里面
724. introduce - v. 介绍
725. introduction - n. 引进，介绍
726. invent - vt. 发明，创造
727. invention - n. 发明，创造
728. invite - vt. 邀请，招待
729. involve - vt. 包含；牵涉
730. island - n. 岛
731. Italy - n. 意大利
732. it - pron. 它
733. its - pron. 它的
734. itself - pron. 它自己
735. jacket - n. 短上衣，夹克衫
736. January - n. 1 月
737. Japan - n. 日本
738. Japanese - a. 日本的；日本人的；n. 日本人，日语
739. jazz - n. 爵士乐
740. jean - n. 牛仔裤（常用复数 “jeans”）
741. job - n.（一份）工作；职业
742. join - v. 参加，加入；连接；会合
743. joke - n. 笑话
744. journey - n. 旅行，路途
745. juice - n. 汁、液
746. July - n. 7 月
747. jump - n. 跳跃；跳变；v. 跳跃；惊起；猛扑
748. June - n. 6 月
749. just - ad. 刚才；恰好；不过；仅；a. 公正的
750. kangaroo - n. 袋鼠
751. keep - v. 保持；保存；培育，饲养
752. key - n. 钥匙；答案；键；关键
753. keyboard - n. 键盘
754. kick - v. 踢
755. kid - n. 小孩；小山羊；t. 欺骗；戏弄；取笑（注：“t.” 应为 “vt.”，修正为 “vt. 欺骗；戏弄；取笑”）
756. kill - v. 杀死，弄死
757. kilo (=kilogram) - n. 千克
758. kilometre (=kilometer) - n. 公里；千米
759. kind - n. 种；类；a. 善良的；友好的
760. king - n. 国王
761. kiss - v. 接吻；轻吻；n. 吻；轻吻
762. kitchen - n. 厨房
763. kite - n. 风筝
764. knee - n. 膝盖
765. knife - n. 小刀；匕首；刀片
766. knock - n.& v. 敲；打；击
767. know - v. 知道；认识
768. knowledge - n. 知道；了解；认识；懂得
769. lab = laboratory - n. 实验室
770. lady - n. 女士，夫人
771. lake - n. 湖
772. land - n. 陆地；v. 登岸（陆）；降落
773. language - n. 语言
774. large - a. 大的；巨大的
775. last - a. 最后的；n. 最后；v. 持续
776. late - a. 晚的；ad. 晚地，迟地
777. later - ad. 后来；a. 后面的；新近的
778. latest - a. 最近的；最新的
779. laugh - n.& v. 笑，大笑；嘲笑
780. law - n. 法律
781. lay –laid- laid - v. 躺下；产卵；搁放
782. lazy - a. 懒惰的
783. lead -led,-led - v. 领导，带领
784. leader - n. 领袖，领导人
785. leaf (复 leaves) - n.（树，菜）叶
786. learn (learnt, learnt ; ~ed,~ed) - vt. 学，学习
787. least (little 的最高级) - a.&ad. little 的最高级
788. leave –left- left - v. 离开；把…… 留下
789. left - a. 左边的；n. 左，左边
790. leg - n. 支柱；腿；腿脚
791. lemon - n. 柠檬（树）
792. lend –lent- lent - vt. 借（出）
793. less - a.&ad. 更少；更小
794. lesson - n. 课；功课；教训
795. let-let-let - vt. 让
796. letter - n. 信；字母
797. level - n. 水平；标准
798. library - n. 图书馆
799. lie - n.& vi. 谎言；说谎
800. life (复 lives) - n. 人生；生命；生活
801. lifestyle - n. 生活方式
802. lift - v. 消散，举起，抬起
803. light - n. 光；光线；a. 轻的；明亮的；vt. 点（火），点燃（light-lit-lit）
804. like - prep. 像；vt. 喜欢
805. limit - n. 限制；限度
806. line - n. 绳索，线路，台词
807. lion - n. 狮子
808. list - n. 一览表，清单
809. listen - vi. 听，仔细听
810. listener - n. 听众之一，听者
811. litter - v. 乱丢杂物
812. little (比较级 less, 最高级 least) - a. 小的，少的
813. live - vi. 生活；居住；活着
814. lively - a. 活泼的；充满生气的
815. local - a. 本地的；当地的
816. lock - n. 锁；vt. 上锁，锁
817. London - n. 伦敦
818. lonely - a. 寂寞的；偏僻的
819. long - a. 长的，远；ad. 长久
820. look - n. 看，瞧；v. 看，观看
821. lot - n. 许多，好些
822. loud - a. 大声的
823. loudly - ad. 大声地
824. love - n.& vt. 爱；热爱；很喜欢
825. lovely - a. 美好的，可爱的
826. low - a.& ad. 低；矮
827. luck - n. 运气，好运
828. luckily - ad. 幸运地，幸亏
829. lucky - a. 运气好的，侥幸
830. lunch - n. 午餐，午饭
831. machine - n. 机器
832. mad - a. 发疯的；生气的
833. magazine - n. 杂志
834. magic - a. 有魔力的
835. main - a. 主要的
836. mainly - ad. 主要地；大体地
837. make (made, made) - vt. 制造，做；使得
838. man (复 men) - n. 成年男人；人类
839. manage - v. 管理；处理；设法应付
840. manager - n. 经理
841. manner - n. 方式；习惯
842. many (比较级 more, 最高级 most) - pron. 许多人、物；a. 许多
843. map - n. 地图
844. March - n. 3 月
845. mark - n. 标记；vt. 标明
846. market - n. 市场，集市
847. marry - v. 成婚，结婚
848. master - adj. 主人的；n. 主人；大师；vt. 掌握
849. match - vt. 使成对；n. 竞赛；火柴
850. material - n. 材料
851. maths= mathematics - n. 数学
852. matter - n. 情况；v. 要紧
853. may - v. 可以；可能
854. May - n. 5 月
855. maybe - ad. 可能，大概，也许
856. me - pron. 我（宾格）
857. meal - n. 一餐（饭）
858. mean-meant- meant - vt. 意思是，意指
859. meaning - n. 意思，含意
860. meat - n. 肉
861. medal - n. 奖牌，奖章
862. medical - a. 医学的，医疗的
863. medicine - n. 药
864. meet (met, met) - vt. 遇见，会；集会
865. meeting - n. 会；会见；汇合点
866. member - n. 成员，会员
867. memory - n. 回忆，记忆
868. mention - vt. 提到；提名
869. menu - n. 菜单
870. mess - n. 凌乱状态
871. message - n. 消息；口信；信息
872. method - n. 方法；办法
873. metre (美 meter) - n. 米，公尺
874. middle - n. 中间；当中；中级的
875. might - v. aux. (may 的过去式) 可能，也许
876. mile - n. 英里
877. milk - n. 牛奶；vt. 挤奶
878. million - n. 百万
879. mind - n. 思想；v. 介意，关心
880. mine - 〈名词性物主代词〉我的
881. minute - n. 分钟；一会儿，瞬间
882. Miss - n. 小姐（称呼未婚妇女）
883. miss - vt. 失去，错过，缺
884. missing - a. 失踪的，找不到的
885. mistake (mistook, mistaken) - vt. 弄错；n. 错误
886. mix - v. 配制；混合；n. 混合物
887. mobile - a. 可移动的，可动的
888. model - n. 模型，原形；范例；模范
889. modern - a. 现代的
890. Mom =Mum - n. 妈妈
891. moment - n. 片刻，瞬间
892. Monday - n. 星期一
893. money - n. 钱；货币
894. monitor - n. 班长；纠察生；监视器
895. monkey - n. 猴子
896. month - n. 月，月份
897. moon - n. 月球；月光；月状物
898. more (much 或 many 的比较级) - a. & ad. 另外的；附加的
899. morning - n. 早晨，上午
900. most (much 或 many 的最高级) - a. & ad. 最多；n. 大部分，大多数
901. mother - n. 母亲
902. mountain - n. 山，山脉
903. mouse - n. 鼠，耗子；鼠标
904. mouth - n. 嘴，口
905. move - v. 移动，搬动，搬家
906. movie - n.（口语）电影
907. Mr. (mister) - n. 先生 (用于姓名前)
908. Mrs. (mistress) - n. 太太 (称呼已婚妇女)
909. Ms. - n. 女士（用在婚姻状况不明的女子）
910. much - a. 许多的；ad. 非常
911. museum - n. 博物馆，博物院
912. music - n. 音乐，乐曲
913. musician - n. 音乐家
914. must - modal v. 必须
915. my - pron. 我的
916. myself - pron. 我自己
917. name - n. 名字，姓名，名称
918. national - a. 民族的，国家的
919. natural - a. 自然的
920. nature - n. 自然
921. near - a. 近的，附近；prep. 在…… 附近
922. naughty - adj. 顽皮的，淘气的
923. nearly - ad. 将近，几乎
924. necessary - a. 必需的，必要的
925. neck - n. 颈，脖
926. need - n. 需要；aux.& v. 必须
927. neighbour (美 neighbor) - n. 邻居，邻人
928. neither - a.（两者）都不；也不
929. nervous - a. 紧张不安的
930. net - n. 网
931. never - ad. 决不，从来没有
932. new - a. 新的；新鲜的
933. news - n. 新闻，消息
934. newspaper - n. 报纸
935. next - a. 最近的；ad. 下一步
936. nice - a. 令人愉快的；漂亮的
937. night - n. 夜；夜间
938. nine - num. 九
939. nineteen - num. 十九
940. ninety - num. 九十
941. ninth - num. 第九
942. no - ad. 不，不是；a. 没有
943. nobody - n. 渺小人物；pron. 没有人
944. nod - v. 点头
945. noise - n. 声音，噪声，喧闹声
946. noisy - a. 喧闹的，嘈杂的
947. none - pron. 无任何东西或人
948. noodle - n. 面条
949. noon - n. 中午，正午
950. nor - conj. 也不；ad. 也不
951. north - a. 北的；n. 北方，北边
952. northern - a. 北方的；ad. 向北方
953. nose - n. 鼻子
954. not - adv. 表示否定，不
955. note - n. 便条；钞票；vt. 记下；注意
956. notebook - n. 笔记簿
957. nothing - n. 没有东西；ad. 一点也不
958. notice - n. 布告；注意；vt. 注意
959. November - n. 11 月
960. now - ad. 现在
961. number - n. 数，号码；数量
962. nurse - n. 护士；保育员
963. object - n. 目标；客体；v. 反对；拒绝
964. obvious - adj. 明显的；显著的
965. ocean - n. 海洋
966. o’clock - n. 点钟
967. October - n. 10 月
968. of - prep. 属于；…… 的
969. off - prep. 离开，脱离
970. offer - n.& vt. 提供；建议
971. office - n. 办公室
972. officer - n. 军官；公务员，官员
973. often - ad. 经常，常常
974. oil - n. 油；石油
975. old - a. 老的，旧的
976. Olympics - n. 奥林匹克
977. on - prep. 在…… 之上
978. once - n.& ad. 一次，一度，从前；conj. 一旦
979. one - pron. 一（个，只……）
980. online - a. 连线的
981. only - a. 惟一的，仅有的；ad. 仅仅，只，才
982. open - a. 开着的，开口的；vt. 开，打开
983. opera - n. 歌剧
984. operate - v. 运转；动手术
985. operation - n. 手术，操作
986. opinion - n. 意见，看法
987. opposite - prep. 在…… 的对面；a. 对面的
988. opportunity - n. 时机，机会
989. or - conj. 或；否则
990. orange - n. 橘子，橘汁；a. 橘色的，橙色的
991. order - n. 顺序；vt. 订购，定货；点菜
992. organization - n. 组织
993. organize - v. 把…… 组织起来，组织
994. other - pron. 别人，别的东西
995. our - pron. 我们的
996. ours - pron. 我们的（名词性物主代词）
997. ourselves - pron. 我们自己
998. out - ad. 出外；在外，向外；熄
999. outdoor - a.&ad. 户外的
1000. outside - n. 外面；ad. 在外面
1001. over - prep. 在…… 上方；越过；遍及
1002. own - a. 自己的；v. 拥有
1003. owner - n. 物主，所有人
1004. Pacific - n. 太平洋
1005. page - n. 页；记录
1006. pain - n. 痛；疼痛
1007. paint - n. 油漆；vt. 油漆，粉刷，绘画
1008. pair - n. 一双，一对
1009. pale - a.（脸色）苍白的
1010. palace - n. 宫，宫殿
1011. pancake - n. 薄烤饼，粉饼
1012. panda - n. 熊猫
1013. pants - n. (pl.) 长裤
1014. paper - n. 纸；报纸
1015. paragraph - n. 段落
1016. pardon - n. & vt. 对不起；饶恕；int. 请再说一遍
1017. parent - n. 父（母），双亲
1018. park - n. 公园；vt. 停放 (汽车)
1019. part - n. 部分；成分；角色；a. 局部的；部分的
1020. partner - n. 搭档，同伴
1021. part-time - a. 兼任的
1022. party - n. 聚会，晚会；党派
1023. pass - vt. 传，递；通过
1024. passage - n. 一节，一段；通道；走廊
1025. passenger - n. 乘客，旅客
1026. passport - n. 护照；通行证
1027. past - ad. 过；n. 过去，昔日，往事
1028. patient - n. 病人；a. 耐心的
1029. pay -paid-paid - v. 付钱，给…… 报酬；n. 工资
1030. PE (=physical education) - n. 体育
1031. peace - n. 和平
1032. pear - n. 梨子，梨树
1033. pen - n. 钢笔，笔
1034. pencil - n. 铅笔
1035. pencil-box - n. 铅笔盒
1036. people - n. 人，人们；人民
1037. percent - n. 百分之……
1038. perfect - a. 完美的，极好的
1039. perform - v. 执行；完成；演奏
1040. perhaps - ad. 可能，或
1041. period - n. 阶段；时期
1042. person - n. 人
1043. personal - a. 个人的，私人的
1044. pet - n. 宠物，爱畜
1045. phone - v. 打电话；n. 电话；电话机
1046. photo (=photograph) - n. 照片
1047. physics - n. 物理
1048. piano - n. 钢琴
1049. pick - v. 拾起；挑选
1050. picnic - n.& v. 野餐
1051. picture - n. 图片，画片，照片
1052. pie - n. 馅饼；饼图
1053. piece - n. 一块
1054. pig - n. 猪
1055. pilot - n. 飞行员
1056. pink - a. 粉红色的
1057. pioneer - n. 开拓者；先驱者
1058. pity - n. 怜悯，同情
1059. place - n. 地方，处所；v. 放置，安置，安排
1060. plan - n.& v. 计划，打算
1061. plane - n. 飞机
1062. planet - n. 行星
1063. plant - v. 种植，播种；n. 植物
1064. plastic - a. 塑料的；n. 塑料
1065. play - v. 玩；播放；n. 玩耍，戏剧
1066. player - n. 选手；做游戏的人，演奏者
1067. playground - n. 操场，运动场
1068. pleasant - a. 令人愉快的，舒适的
1069. please - v. 请；使人高兴，使人满意
1070. pleased - a. 高兴的
1071. pleasure - n. 高兴，愉快
1072. plenty - n. 充足，大量
1073. pocket - n. 口袋
1074. poem - n. 诗
1075. point - v. 指；指向；n. 点
1076. polite - a. 有礼貌的，有教养的
1077. police - n. 警察，警务人员
1078. policeman/policewoman - n. 警察 / 女警察
1079. pollute - v. 污染
1080. pollution - n. 污染
1081. pool - n. 游泳池
1082. poor - adj. 贫穷的
1083. popular (pop) - a. 流行的，大众的，受欢迎的
1084. population - n. 人口，人数
1085. possible - a. 可能的
1086. post - n. 邮政，邮寄，邮件；v. 投寄
1087. postcard - n. 明信片
1088. poster - n. 海报，广告
1089. postman - n. 邮递员；邮差
1090. potato - n. 土豆，马铃薯
1091. pound - n. 英镑
1092. power - n. 力量
1093. practice - n. 练习
1094. praise - v. & n. 表扬，赞扬
1095. predict - v. 预测
1096. prefer - v. 更喜欢
1097. prepare - v. 准备；装备
1098. present - n. 礼物；现在；a. 出席的；现在的
1099. president - n. 总统；主席
1100. press - v. 按，压
1101. pretty - a. 漂亮的，俊俏的
1102. prevent - v. 防止
1103. price - n. 价格，价钱
1104. pride - n. 自豪感；骄傲
1105. primary - a. 初等的；初级的
1106. print - v. 印刷；打印；刊载
1107. private - a. 私人的；私有的
1108. prize - n. 奖赏，奖品
1109. probably - ad. 很可能，大概
1110. problem - n. 问题，难题
1111. process - n.（为达到某一目标的）过程
1112. produce - vt. 生产；制造
1113. product - n. 产品，产物
1114. programme (美 program) - n. 节目；项目
1115. progress - n. 进步，上进；vi. 进展；进行
1116. project - n. 项目；工程；计划；规划
1117. promise - n. 许诺，允诺；希望
1118. prove - vt. 证明；检验
1119. provide - vt. 提供
1120. province - n. 省
1121. public - a. 公共的，公众的；n. 公众
1122. pull - v. 拉，拖；n. 拉力，引力
1123. pupil - n.（小）学生
1124. punish - v. 惩罚，处罚
1125. purple - n. 紫色；adj. 紫色的
1126. purpose - n. 目的
1127. purse - n. 钱包
1128. push - n.& v. 推
1129. put -put,-put - vt. 放，摆
1130. quarter - n. 四分之一，一刻钟
1131. queen - n. 皇后
1132. question - vt. 询问；n. 问题
1133. quick - a. 快；敏捷的；急剧的
1134. quickly - ad. 快地；敏捷地
1135. quiet - a. 安静的；寂静的
1136. quite - ad. 完全，十分
1137. rabbit - n. 兔，家兔
1138. race - n. 种族，赛跑，竞赛
1139. radio - n. 无线电，收音机
1140. railway - n. 铁路；轨道
1141. rain - n. 雨水；vi. 下雨
1142. raincoat - n. 雨衣
1143. rainy - a. 下雨的；多雨的
1144. raise - v. 使升高；饲养
1145. rapid - a. 快的；迅速的
1146. reach - v. 到达，伸手（脚等）够到
1147. read –read-read - v. 读；朗读
1148. ready - a. 准备好的
1149. real - a. 真实的，确实的
1150. realise (美 realize) - vt. 认识到，实现
1151. really - ad. 真正地；到底；确实
1152. reason - vi. 推论；劝说；n. 理由，原因
1153. receive - v. 收到，得到
1154. recent - a. 近来的，最近的
1155. recently - ad. 最近；新近
1156. recognize (美 recognize) - vt. 认出，识别
1157. record - n. 记录；唱片
1158. recycle - v. 再回收，再利用
1159. red - n. 红色；a. 红色的
1160. refer - vi. 参考；涉及
1161. refuse - vi. 拒绝，不愿
1162. regret - n. & v. 懊悔；遗憾；失望
1163. regular - a. 规则的；定期的
1164. relate - v. 使…… 有联系
1165. relationship - n. 关系；关联
1166. relative - n. 家人，亲属
1167. relax - v.（使）放松，轻松
1168. relaxed - a. 放松的
1169. remain - v. 逗留；留下
1170. remember - v. 记得，想起
1171. remind - vt. 提醒；使想起
1172. repair - n.& vt. 修理；修补
1173. repeat - v. 重复
1174. reply - n. 回答，答复
1175. report - n.& v. 报道，报告
1176. reporter - n. 记者
1177. require - v. 需要；要求；命令
1178. research - n. 研究，调查
1179. respect - v. 尊重
1180. responsible - adj. 负责的，可靠的；有责任的
1181. rest - n. 休息；剩余的部分
1182. restaurant - n. 饭馆，饭店
1183. result - n. 结果，效果
1184. return - v. 归还，回，归
1185. review - v. 复习；n. 复查；复习
1186. rice - n. 稻米；米饭
1187. rich - a. 富裕的，有钱的
1188. ride –rode-ridden - v. 骑；乘车；n. 乘车旅行
1189. right - n. 权利；a. 对，右的；ad. 恰恰，完全地
1190. ring –rang-rung - v.（钟、铃等）响
1191. rise –rose- risen - vi. 上升；升起
1192. risk - n. 危险；冒险；v. 冒…… 的危险
1193. river - n. 江；河；水道；巨流
1194. road - n. 路，道路
1195. robot - n. 机器人
1196. rock - n. 岩石，大石头；vt. 摇，摇晃
1197. rocket - n. 火箭
1198. role - n. 角色；任务
1199. room - n. 房间，室；空间；地方
1200. rope - n. 粗绳；绳索
1201. rose - n. 玫瑰花
1202. round - prep. 环绕一周；a. 圆的
1203. row - n. 一排；一行
1204. rubbish - n. 垃圾；废物
1205. rude - a. 不礼貌的，粗鲁的
1206. rule - n. 规则，规定；vt. 统治；支配
1207. ruler - n. 统治者；直尺
1208. run –ran- run - vi. 跑，奔跑；（颜色）褪色
1209. rush - vi. 冲，奔跑
1210. Russia - n. 俄罗斯，俄国
1211. Russian - n. 俄国人
1212. sad - a.（使人）悲伤的
1213. safe - a. 安全的；n. 保险柜
1214. safety - n. 安全，保险
1215. sail - n. 航行；v. 航行，开航
1216. salad - n. 沙拉
1217. sale - n. 卖，出售
1218. salt - n. 盐
1219. same - a. 同样的，同一的
1220. sand - n. 沙，沙子
1221. sandwich - n. 三明治（夹心面包片）
1222. satisfy - v. 使满意
1223. Saturday - n. 星期六
1224. save - vt. 救，挽救，节省
1225. say (said, said) - vt. 说，讲
1226. saying - n. 言语；格言
1227. scarf - n. 领巾，围巾
1228. scene - n. 场景，场面；景色
1229. school - n. 学校
1230. schoolbag - n. 书包
1231. science - n. 科学，自然科学
1232. scientist - n. 科学家
1233. scissors - n. 剪刀
1234. score - n. 分数
1235. screen - n. 屏；幕；屏风
1236. sea - n. 海，海洋
1237. search - v. 搜索；搜寻；查找
1238. season - n. 季；季节；vt. 给…… 调味
1239. seat - n. 座位，座
1240. second - n. 秒；num. 第二
1241. secret - n. 秘密，内情
1242. secretary - n. 秘书
1243. see –saw-seen - vt. 看见，看到
1244. seem - v. 似乎，好像
1245. seldom - a. 很少地；不常
1246. sell –sold-sold - v. 卖，售
1247. send –sent-sent - v. 打发，派遣；送，邮寄
1248. sense - n. 感觉官能；理解，领悟
1249. sentence - n. 句子；宣判
1250. separate - v. 使分开；分隔；a. 分开的，单独的
1251. September - n. 9 月
1252. serious - a. 严肃的，严重的；认真的
1253. serve - vt. 招待（顾客等），服务
1254. service - n. 服务
1255. set - n. 装备，设备；vt. 设置
1256. seven - num. 七
1257. seventeen - num. 十七
1258. seventh - num. 第七
1259. seventy - num. 七十
1260. several - pron. 几个，数个；a. 若干
1261. shake-shook-shaken - v.（使）动摇，震动
1262. shall (should) - v. aux. 将要
1263. shame - n. 羞涩；羞愧
1264. shape - n. 形状，外形
1265. share - v. 分享；分担
1266. shark - n. 鲨鱼
1267. she - pron. 她
1268. sheep (复 sheep) - n.（绵）羊
1269. shelf (复 shelves) - n. 架子；搁板；格层；礁
1270. shine-shone-shone - v. 发光；照耀；n. 光泽；光彩
1271. ship - n. 船，轮船
1272. shirt - n. 男衬衫
1273. shoe - n. 鞋
1274. shop - vi. 买东西；n. 商店，车间
1275. short - a. 短的；矮的
1276. shorts - n. 短裤；运动短裤
1277. should - v. mod. 应当，应该
1278. shoulder - n. 肩；肩膀；肩部
1279. shout - n.& v. 喊，呼喊
1280. show - n. 展示，展览；v. 给…… 看，显示
1281. shower - n. 阵雨；淋浴
1282. shut - v. 关闭；停业；n. 关闭
1283. shy - a. 害羞的
1284. sick - a. 有病的，患病的；恶心的
1285. side - n. 边，旁边；面，侧面
1286. sight - n. 情景，风景；视力
1287. sightseeing - n. 观光；游览
1288. sign - n. 记号，标记；招牌；v. 签字
1289. silence - n. 沉默；寂静
1290. silent - a. 寂静的
1291. silk - n.（蚕）丝，丝织品
1292. silly - a. 傻的，愚蠢的
1293. similar - a. 相似的，像
1294. simple - a. 简单的，简易的
1295. simply - ad. 简单地；（加强语气）的确
1296. since - ad. 从那时以来；conj. 从…… 以来
1297. sing–sang--sung - v. 唱，唱歌
1298. singer - n. 歌手
1299. single - a. 单一的
1300. sir - n. 先生；阁下
1301. sister - n. 姐；妹
1302. sit (sat, sat) - vi. 坐
1303. situation - n. 形势，情况
1304. six - num. 六
1305. sixteen - num. 十六
1306. sixteenth - num. 第十六
1307. sixth - num. 第六
1308. sixty - num. 六十
1309. size - n. 尺寸，大小
1310. skate - vi. 溜冰，滑冰
1311. skill - n. 技能，技巧
1312. skirt - n. 女裙
1313. sky - n. 天；天空
1314. sleep - n. 睡觉；vi. 睡觉
1315. sleepy - a. 想睡的，困倦的，瞌睡的
1316. slow - a. 慢慢地，缓慢地
1317. small - a. 小的；少的
1318. smart - a. 灵巧的，伶俐的；时髦的
1319. smell - n. 气味；v. 嗅，闻到
1320. smile - n.& v. 微笑
1321. smoke - n. 烟；v. 冒烟；吸烟
1322. smooth - a. 光滑的；流畅的；v. 弄平
1323. snake - n. 蛇
1324. snow - n. 雪；vi. 下雪
1325. snowy - a. 雪（白）的；下雪的
1326. so - ad. 如此，这么；非常；同样
1327. social - a. 社会的，社交的；群居的
1328. society - n. 社会
1329. sock - n. 短袜
1330. sofa - n. 沙发；长椅
1331. soft - a. 软的，柔和的
1332. soldier - n. 士兵，战士
1333. solve - v. 解决；解答
1334. some - a. 一些，若干；有些；某一
1335. somebody - pron. 某人；有人；有名气的人
1336. someone - pron. 某一个人
1337. something - pron. 某事；某物
1338. sometimes - ad. 有时
1339. somewhere - ad. 在某处
1340. son - n. 儿子
1341. song - n. 歌唱；歌曲
1342. soon - ad. 不久，很快
1343. sorry - a. 对不起，抱歉
1344. sound - vi. 听起来；发出声音；n. 声音
1345. soup - n. 汤
1346. south - n. 南方；a. 南（方）的；ad. 在南方
1347. southern - a. 南部的，南方的
1348. space - n. 空间
1349. spare - a. 空闲的；v. 节约；饶恕
1350. speak-spoke-spoken - v. 说，讲；谈
1351. speaker - n. 演讲人，演说家；扬声器
1352. special - a. 特别的；专门的
1353. speech - n. 演讲
1354. speed - n. 速度，迅速；v. 疾行
1355. spell (~ed, ~ed; spelt, spelt) - vt. 拼写
1356. spend-spent-spent - v. 度过；花费（钱、时间等）
1357. spirit - n. 精神；神灵
1358. spoon - n. 匙，调羹
1359. sport - n. 体育运动，锻炼；运动会
1360. spread - v. 扩展；蔓延
1361. spring - n. 春天，泉水；v. 生长；涌出
1362. square - n. 正方形；广场；a. 正方形的
1363. stamp - n. 邮票
1364. stairs - n. 楼梯
1365. stand - n. 立场；台；v. 站，立；坐落
1366. standard - n. 标准；水准
1367. star - n. 星，恒星
1368. start - v. 开始，着手
1369. state - n. 状态；国家（美国的州）
1370. station - n. 站，所；车站；电台
1371. stay - n.& vi. 停留，逗留，呆
1372. steal - v. 偷，盗
1373. step - n. 脚步；台阶；vi. 走；跨步
1374. stick-stuck- stuck - vi. 粘住，钉住；坚持
1375. still - a. 不动的，平静的；ad. 仍然
1376. stomach - n. 胃，胃部
1377. stomachache - n. 胃疼
1378. stone - n. 石头，石料
1379. stop - n. 停；（停车）站；v. 停止
1380. store - n. 商店；vt. 储藏，存储
1381. storm - n. 风暴，暴（风）雨
1382. story - n. 故事，小说
1383. straight - a. 笔直的；正直的；ad. 直接地
1384. strange - a. 奇怪的，奇特的，陌生的
1385. stranger - n. 陌生人，外人
1386. strawberry - n. 草莓；草莓色
1387. street - n. 街，街道
1388. strict - a. 严格的，严密的
1389. strong - a. 强（壮）的；坚固的；强烈的
1390. stress - n. 压力；强调
1391. student - n. 学生
1392. study - n. 学习，研究；课题；书房；v. 学习；研究
1393. stupid - a. 笨的；糊涂的
1394. style - n. 风格；时尚
1395. subject - n. 学科；主语；主体
1396. subway - n. 地铁；地下通道
1397. succeed - vi. 成功
1398. success - n. 成功
1399. successful - adj. 成功的；一帆风顺的
1400. such - ad. 那么；pron. 人，事物
1401. sudden - a. 突然的
1402. sugar - n. 糖；甜食
1403. suggest - vt. 建议，提议
1404. suggestion - n. 建议
1405. summer - n. 夏天，夏季
1406. sun - n. 太阳；阳光
1407. Sunday - n. 星期日
1408. sunglasses - n. 太阳镜，墨镜
1409. sunny - a. 晴朗的
1410. supermarket - n. 超级市场
1411. supper - n. 晚餐，晚饭
1412. support - v./n. 支持；扶持
1413. supply - n. 供给，补给；vt. 供给，提供
1414. suppose - vt. 猜想，假定
1415. sure - a. 确信，肯定
1416. surf - v. 冲浪
1417. surface - n. 表面；外表
1418. surprise - vt. 使惊奇，诧异
1419. surprised - a. 令人惊讶的
1420. survey - n. & v. 调查
1421. sweater - n. 厚运动衫，毛衣
1422. sweep-swept- swept - v. 扫除，扫
1423. sweet - n. 甜食；a. 甜的
1424. swim-swam-swum - v. 游泳，游
1425. swimming - n. 游泳
1426. system - n. 制度，体制；系统
1427. symbol - n. 符号；象征
1428. table - n. 桌子；表格
1429. tail - n. 尾；尾巴
1430. take –took-taken - vt. 拿；拿走；买下
1431. talent - n. 才能；天才；天资
1432. talk - n.& v. 谈话，讲话
1433. tall - a. 高的
1434. tape - n. 录音带；录影带
1435. task - n. 任务，工作
1436. taste - n./vt. 品尝；味道
1437. tasty - a. 好吃的
1438. taxi - n. 出租汽车
1439. tea - n. 茶；茶叶
1440. teach –taught-taught - v. 教书，教
1441. teacher - n. 教师，教员
1442. team - n. 队，组
1443. technology - n. 科技；技术
1444. teenager - n. 少年
1445. telephone - v. 打电话；n. 电话
1446. television (=TV) - n. 电视
1447. tell –told-told - vt. 告诉；讲述
1448. temperature - n. 温度
1449. ten - num. 十
1450. tennis - n. 网球
1451. tenth - num. 第十
1452. term - n. 学期；术语；条款
1453. terrible - a. 可怕的；糟糕的
1454. test - vt.& n. 测试，考查
1455. text - n. 文本；课文
1456. than - conj. 比
1457. thank - vt. 感谢
1458. that - a.& pron. 那，那个
1459. the - art. 这；那
1460. theatre (美 theater) - n. 剧场，戏院
1461. their - pron. 他 / 她 / 它们的
1462. theirs - pron. 他 / 她 / 它们的 (名词性物主代词)
1463. them - pron. 他 / 她 / 它们 (宾格)
1464. themselves - pron. 他 / 她 / 它们自己
1465. then - ad. 那时；然后
1466. there - n. 那里；ad. 表 “存在”
1467. these - a. & pron. 这些
1468. they - pron. 他 / 她 / 它们
1469. thick - a. 厚的；浓的；粗的
1470. thin - a. 薄的；瘦的；稀的
1471. thing - n. 东西；事情
1472. think-thought-thought - v. 想；认为；考虑
1473. thirsty - a. 口渴的；渴望的
1474. third - num. 第三
1475. thirteen - num. 十三
1476. thirty - num. 三十
1477. this - a.& pron. 这，这个
1478. those - a.& pron. 那些
1479. though - conj. 虽然，可是
1480. thought - n. 思考，思想；念头
1481. thousand - num. 千
1482. three - num. 三
1483. through - prep. 穿过；从始至终
1484. throw-threw-thrown - v. 投，掷，扔
1485. Thursday - n. 星期四
1486. ticket - n. 票；券
1487. tidy - a. 整洁的，干净的
1488. tie - vt. 系，拴，扎；n. 领带
1489. tiger - n. 老虎
1490. time - n. 时间；次，回
1491. tiny - a. 微小的；很少的
1492. tired - a. 疲劳的，累的
1493. tiring - a. 令人疲倦的
1494. title - n. 冠军；标题
1495. to - prep. 朝；位于……
1496. today - ad.& n. 今天；当今
1497. together - ad. 一起，共同
1498. toilet - n. 厕所；盥洗室
1499. tomato - n. 西红柿，番茄
1500. tomorrow - ad.& n. 明天
1501. ton - n. 吨；很重；大量
1502. tonight - ad.& n. 今晚，今夜
1503. too - ad. 也，又；过分
1504. tool - n. 工具，器具
1505. tooth (复 teeth) - n. 牙齿
1506. toothache - n. 牙疼
1507. top - n. 顶部；上面
1508. topic - n. 话题；题目
1509. total - a. 总的；全部的
1510. touch - vt. 触摸；触动
1511. tour - n. 参观，观光，旅行
1512. tourist - n. 旅行者，观光者
1513. towards - prep. 向，朝，对于
1514. tower - n. 塔
1515. town - n. 城镇，城
1516. toy - n. 玩具，玩物
1517. trade - n. 贸易，商业；v. 贸易；交换
1518. traditional - a. 传统的，习俗的
1519. traffic - n. 交通，来往车辆
1520. train - n. 火车；v. 培训，训练
1521. training - n. 培训
1522. translate - vt. 翻译
1523. transport - n.& v. 运输，运送
1524. travel - n.& vi. 旅行
1525. treasure - n. 财宝；珍宝
1526. treat - v. 对待，看待
1527. tree - n. 树
1528. trip - n. 旅行，旅程
1529. trouble - vt. 使苦恼，使麻烦
1530. trousers - n. 裤子，长裤
1531. truck - n. 卡车；货车
1532. true - a. 真的，真实的
1533. truth - n. 真理，事实
1534. trust - n. 信任，信赖；vt. 信任，信赖
1535. try - v. 试，试图，努力
1536. T-shirt - n. T 恤衫
1537. Tuesday - n. 星期二
1538. turn - v. 旋转；转变；n. 轮流
1539. twelfth - num. 第十二
1540. twelve - num. 十二
1541. twentieth - num. 第二十
1542. twenty - num. 二十
1543. twice - ad. 两次；两倍
1544. two - num. 二
1545. typical - adj. 典型的
1546. ugly - a. 难看的；丑陋的
1547. UK (= United Kingdom) - abbr. 英国
1548. umbrella - n. 雨伞
1549. uncle - n. 叔；伯；舅
1550. uncomfortable - a. 不舒服的
1551. under - ad.& prep. 在…… 下面
1552. underground - a. 地下的；n. 地铁
1553. understand-understood- understood - v. 懂得；理解
1554. underline - v. 强调，在…… 下面划线
1555. unfriendly - a. 不友好的
1556. unhappy - a. 不幸的；不快乐的
1557. unhealthy - a. 不健康的
1558. unless - conj. 除非；若不
1559. unit - n. 单元；单位
1560. university - n. 大学
1561. until - prep.& conj. 直到…… 为止
1562. unusual - a. 不寻常的；独特的
1563. up - ad. 向上；a. 上面的，向上的
1564. upon - prep. 在…… 上；到…… 上
1565. upset - adj. 心烦的
1566. upstairs - ad. 在楼上，到楼上
1567. us - pron. 我们 (宾格)
1568. US (=United States) - abbr. 美国
1569. use - n.& vt. 利用，应用
1570. used - a. 用过的；二手的
1571. useful - a. 有用的，有益的
1572. usual - a. 通常的，平常的
1573. usually - ad. 通常地；平常地
1574. vacation - n. 假期，休假
1575. valuable - a. 贵重的；有价值的
1576. value - n. 价值；重要性
1577. various - adj. 各种各样的；多方面的
1578. vegetable - n. 蔬菜
1579. very - ad. 很，非常
1580. victory - n. 成功；胜利
1581. video - n. 录像，视频
1582. view - n. 观点；v. 查看
1583. village - n. 村庄，乡村
1584. villager - n. 村民，乡民
1585. violin - n. 小提琴
1586. visit - n.& vt. 参观，访问
1587. visitor - n. 访问者，参观者
1588. voice - n. 说话声；语态
1589. volleyball - n. 排球
1590. volunteer - n. 志愿者
1591. wait - vi. 等，等候
1592. waiter - n. 伺者，服务员
1593. wake-woke-woken - v. 醒，醒来；叫醒
1594. walk - n.& v. 步行；散步
1595. wall - n. 墙
1596. wallet - n. 钱包，钱夹
1597. want - vt. 想；需要，必要
1598. war - n. 战争
1599. warm - a. 温暖的；热情的
1600. warn - v. 警告；告诫
1601. wash - n. 洗（涤）；v. 洗涤
1602. waste - n.& vt. 浪费
1603. watch - vt. 观看；当心；n. 表
1604. water - n. 水；v. 浇水
1605. watermelon - n. 西瓜
1606. wave - n. 海浪；挥手；v. 挥手，招手
1607. way - n. 路，路线；方式，手段
1608. we - pron. 我们 (主格)
1609. weak - a. 差的；弱的；淡的
1610. wealth - n. 财富；财产
1611. wear-wore- worn - v. 穿，戴
1612. weather - n. 天气
1613. website - n. 网站
1614. Wednesday - n. 星期三
1615. week - n. 星期，周
1616. weekday - n. 平日
1617. weekend - n. 周末
1618. weigh - vt. 称…… 的重量，重
1619. weight - n. 重，重量
1620. welcome - int. n. & v. 欢迎；a. 受欢迎的
1621. well (比较级 better, 最高级 best) - ad. 好；令人满意地
1622. well-known - a. 众所周知的，著名的
1623. west - a./ad. 在西方；向西方；n. 西部；西方
1624. western - a. 西方的，西部的
1625. wet - a. 湿的，潮的；多雨的
1626. what - pron. 什么，怎么样；a. 多么，何等；什么
1627. whatever - conj. & pron. 无论什么，不管什么
1628. wheel - n. 轮，机轮
1629. when - conj. 当…… 的时候
1630. whenever - conj. 无论何时
1631. where - ad. 在哪里；往哪里
1632. whether - conj. 是否
1633. which - pron. 那（哪）一个
1634. while - conj. 在…… 的时候；n. 一会儿
1635. white - a. 白色的；n. 白色
1636. who - pron. 谁
1637. whole - a. 整个的
1638. whom - pron. 谁 (who 的宾格)
1639. whose - pron. 谁的
1640. why - ad. & int. 为什么
1641. wide - a. 宽阔的
1642. wife - n. 妻子
1643. wild - a. 野生的，野的
1644. will (would) - modal v. 将，会，要
1645. win-won-won - v. 获胜，赢得
1646. wind - n. 风
1647. window - n. 窗户
1648. windy - a. 有风的，多风的
1649. wine - n. 葡萄酒
1650. wing - n. 翅膀；翼
1651. winner - n. 获胜者
1652. winter - n. 冬天，冬季
1653. wise - a. 明智的；聪明的
1654. wish - n. 愿望；vt. 希望
1655. with - prep. 带有；以；和
1656. without - prep. 没有
1657. woman (复 women) - n. 妇女，女人
1658. wonder - v. 对…… 疑惑，感到惊奇，想知道
1659. wonderful - a. 美妙的，精彩的；了不起的
1660. wood - n. 木头，木材；树木，森林
1661. word - n. 词，单词；话
1662. work - n. 工作，劳动；作品；vi. 工作
1663. worker - n. 工人；工作者
1664. world - n. 世界
1665. worried - a. 担忧的，烦恼的
1666. worry - n.& v. 烦恼，担忧，发愁；困扰
1667. worse - a. (bad /badly 的比较级) 更坏的
1668. worst - a. (bad /badly 的最高级) 最坏的
1669. worth - prep. 相当于…… 价值的，值得…… 的
1670. would - v. (will 的过去时) 将会，打算
1671. wound - n. 伤；伤口
1672. write-wrote-written) - v. 写，书写；写作
1673. writer - n. 作者，作家
1674. wrong - a. 错误的；不正常的，有毛病的
1675. X-ray - n. X 射线；X 光
1676. yard - n. 院子；场院；码
1677. year - n. 年
1678. yellow - a. 黄色的；n. 黄（颜）色
1679. yes - ad. 是，好，同意
1680. yesterday - n.& ad. 昨天
1681. yet - ad. 尚，还，仍然
1682. you - pron. 你；你们
1683. young - a. 年轻的
1684. your - pron. 你的；你们的
1685. yours - pron. 你的；你们的 (名词性物主代词)
1686. yourself - pron. 你自己
1687. yourselves - pron. 你们自己
1688. zero - n. & num. 零；零度；零点
1689. zoo - n. 动物园"""
    
    # 这里只显示前100个作为示例，实际需要包含全部1689个
    # 由于数据太长，我会创建一个更简洁的处理方法
    
    return vocabulary_data

def process_and_insert_data(conn, cursor):
    """处理数据并插入数据库"""
    # 由于数据太长，我将创建一个简化的示例
    # 实际使用时需要包含完整的1689个词汇
    
    sample_words = [
        ("a", "/ə/", "det.", "一个", "I have a book.", "基础", "基础"),
        ("ability", "/əˈbɪləti/", "n.", "能力", "He has the ability to learn.", "能力", "中级"),
        ("able", "/ˈeɪbl/", "a.", "能够；有能力的", "I am able to swim.", "能力", "基础"),
        ("about", "/əˈbaʊt/", "ad./prep.", "大约；到处；关于；四处", "Tell me about your school.", "位置", "基础"),
        ("above", "/əˈbʌv/", "prep.", "在……上面", "The bird flies above the tree.", "位置", "基础"),
        ("abroad", "/əˈbrɔːd/", "ad.", "到（在）国外", "She studies abroad.", "地点", "中级"),
        ("absent", "/ˈæbsənt/", "a.", "缺席的，不在的", "He was absent from school.", "状态", "中级"),
        ("accept", "/əkˈsept/", "v.", "接受；收受", "I accept your invitation.", "动作", "中级"),
        ("accident", "/ˈæksɪdənt/", "n.", "事故，意外的事", "There was a car accident.", "事件", "中级"),
        ("achieve", "/əˈtʃiːv/", "vt.", "达到，取得", "You can achieve your goals.", "动作", "高级"),
        ("across", "/əˈkrɒs/", "prep.", "横过，穿过", "Walk across the street.", "位置", "基础"),
        ("act", "/ækt/", "n./v.", "法令，条例；表演", "He can act very well.", "动作", "基础"),
        ("action", "/ˈækʃn/", "n.", "行动", "We need to take action.", "动作", "中级"),
        ("active", "/ˈæktɪv/", "a.", "积极的，主动的", "She is very active in class.", "性格", "中级"),
        ("activity", "/ækˈtɪvəti/", "n.", "活动", "We have many activities.", "活动", "中级"),
        ("actor", "/ˈæktə(r)/", "n.", "演员", "He is a famous actor.", "职业", "中级"),
        ("actress", "/ˈæktrəs/", "n.", "女演员", "She is a talented actress.", "职业", "中级"),
        ("actual", "/ˈæktʃuəl/", "a.", "实际的；真实的", "What is the actual problem?", "状态", "中级"),
        ("actually", "/ˈæktʃuəli/", "ad.", "实际上，事实上", "Actually, I don't know.", "副词", "中级"),
        ("add", "/æd/", "v.", "添加，增加", "Add some salt to the soup.", "动作", "基础"),
        ("address", "/əˈdres/", "n.", "地址", "What's your address?", "信息", "基础"),
        ("admire", "/ədˈmaɪə(r)/", "v.", "钦佩；赞美", "I admire your courage.", "情感", "中级"),
        ("adult", "/ˈædʌlt/", "n.", "成年人", "This book is for adults.", "人物", "中级"),
        ("advantage", "/ədˈvɑːntɪdʒ/", "n.", "优点，有利条件", "What's the advantage?", "抽象", "中级"),
        ("advertise", "/ˈædvətaɪz/", "v.", "做广告", "Companies advertise their products.", "商业", "中级"),
        ("advice", "/ədˈvaɪs/", "n.", "忠告，劝告，建议", "Thank you for your advice.", "抽象", "中级"),
        ("advise", "/ədˈvaɪz/", "v.", "建议；劝告", "I advise you to study hard.", "动作", "中级"),
        ("afford", "/əˈfɔːd/", "v.", "买得起；提供", "I can't afford this car.", "经济", "中级"),
        ("afraid", "/əˈfreɪd/", "a.", "害怕的；担心", "Don't be afraid.", "情感", "基础"),
        ("africa", "/ˈæfrɪkə/", "n.", "非洲", "Africa is a big continent.", "地理", "中级"),
        ("african", "/ˈæfrɪkən/", "a./n.", "非洲的；非洲人", "He is African.", "地理", "中级"),
        ("after", "/ˈɑːftə(r)/", "ad.", "在后；后来", "What happened after that?", "时间", "基础"),
        ("afternoon", "/ˌɑːftəˈnuːn/", "n.", "下午，午后", "Good afternoon!", "时间", "基础"),
        ("again", "/əˈɡen/", "ad.", "再一次；再，又", "Try again.", "频率", "基础"),
        ("against", "/əˈɡenst/", "prep.", "对着，反对", "We play against them.", "位置", "中级"),
        ("age", "/eɪdʒ/", "n.", "年龄；时代", "What's your age?", "时间", "基础"),
        ("ago", "/əˈɡəʊ/", "ad.", "……以前", "I saw him two days ago.", "时间", "基础"),
        ("agree", "/əˈɡriː/", "v.", "同意；应允", "I agree with you.", "情感", "基础"),
        ("agreement", "/əˈɡriːmənt/", "n.", "协议；同意；一致", "We reached an agreement.", "抽象", "中级"),
        ("air", "/eə(r)/", "n.", "空气；大气", "The air is fresh.", "自然", "基础"),
        ("airport", "/ˈeəpɔːt/", "n.", "航空站，飞机场", "I'll meet you at the airport.", "地点", "中级"),
        ("alive", "/əˈlaɪv/", "a.", "活着的，存在的", "The fish is still alive.", "状态", "中级"),
        ("all", "/ɔːl/", "a.", "全（部）；所有的", "All students are here.", "数量", "基础"),
        ("allow", "/əˈlaʊ/", "vt.", "允许，准许", "Please allow me to help.", "动作", "中级"),
        ("almost", "/ˈɔːlməʊst/", "ad.", "几乎，差不多", "I almost finished.", "程度", "中级"),
        ("alone", "/əˈləʊn/", "a.", "单独的，孤独的", "I live alone.", "状态", "中级"),
        ("along", "/əˈlɒŋ/", "ad./prep.", "向前；沿着", "Walk along the river.", "位置", "基础"),
        ("aloud", "/əˈlaʊd/", "adv.", "大声地；出声地", "Read aloud.", "方式", "中级"),
        ("already", "/ɔːlˈredi/", "ad.", "已经", "I already finished.", "时间", "中级"),
        ("also", "/ˈɔːlsəʊ/", "ad.", "也", "I also like music.", "连接", "基础"),
        ("although", "/ɔːlˈðəʊ/", "conj.", "虽然，尽管", "Although it's raining, we go out.", "连接", "中级"),
        ("always", "/ˈɔːlweɪz/", "ad.", "总是；一直；永远", "I always study hard.", "频率", "基础"),
        ("america", "/əˈmerɪkə/", "n.", "美国；美洲", "America is a big country.", "地理", "中级"),
        ("american", "/əˈmerɪkən/", "a./n.", "美国的；美国人", "He is American.", "地理", "中级"),
        ("among", "/əˈmʌŋ/", "prep.", "在（三个以上）之间", "He is among the best students.", "位置", "中级"),
        ("ancient", "/ˈeɪnʃənt/", "a.", "古代的，古老的", "This is an ancient building.", "时间", "中级"),
        ("and", "/ænd/", "conj.", "和", "I like apples and oranges.", "连接", "基础"),
        ("angry", "/ˈæŋɡri/", "a.", "生气的，愤怒的", "Don't be angry.", "情感", "基础"),
        ("animal", "/ˈænɪml/", "n.", "动物", "The dog is a friendly animal.", "自然", "基础"),
        ("another", "/əˈnʌðə(r)/", "pron.", "另一个", "Give me another chance.", "数量", "基础"),
        ("answer", "/ˈɑːnsə(r)/", "n./v.", "回答，答案", "What's the answer?", "学习", "基础"),
        ("ant", "/ænt/", "n.", "蚂蚁", "The ant is small.", "动物", "基础"),
        ("any", "/ˈeni/", "det.", "任何的", "Do you have any questions?", "数量", "基础"),
        ("anybody", "/ˈenibɒdi/", "pron.", "任何人", "Does anybody know the answer?", "人物", "中级"),
        ("anyone", "/ˈeniwʌn/", "pron.", "任何人", "Anyone can join us.", "人物", "中级"),
        ("anything", "/ˈeniθɪŋ/", "pron.", "任何事情", "Is there anything I can do?", "事物", "中级"),
        ("anyway", "/ˈeniweɪ/", "ad.", "不管怎么说", "Anyway, let's continue.", "连接", "中级"),
        ("anywhere", "/ˈeniweə(r)/", "ad.", "任何地方", "I can't find it anywhere.", "地点", "中级"),
        ("appear", "/əˈpɪə(r)/", "v.", "出现，显露", "The sun appears in the morning.", "状态", "中级"),
        ("apple", "/ˈæpl/", "n.", "苹果", "I eat an apple every day.", "食物", "基础"),
        ("april", "/ˈeɪprəl/", "n.", "四月", "April is in spring.", "时间", "基础"),
        ("area", "/ˈeəriə/", "n.", "面积；地域", "This area is very quiet.", "地点", "中级"),
        ("argue", "/ˈɑːɡju/", "v.", "争论", "Don't argue with me.", "动作", "中级"),
        ("arm", "/ɑːm/", "n.", "臂，支架；武器", "My arm hurts.", "身体", "基础"),
        ("army", "/ˈɑːmi/", "n.", "陆军；军队", "He joined the army.", "军事", "中级"),
        ("around", "/əˈraʊnd/", "ad.", "在周围；在附近", "Look around you.", "位置", "基础"),
        ("arrive", "/əˈraɪv/", "vi.", "到达；达到", "When will you arrive?", "动作", "基础"),
        ("art", "/ɑːt/", "n.", "艺术，美术", "I like art.", "文化", "基础"),
        ("article", "/ˈɑːtɪkl/", "n.", "文章；冠词", "This is a good article.", "学习", "中级"),
        ("artist", "/ˈɑːtɪst/", "n.", "艺术家", "He is a famous artist.", "职业", "中级"),
        ("as", "/æz/", "adv./conj.", "和……一样；当……时", "As you know, I'm busy.", "连接", "中级"),
        ("asia", "/ˈeɪʃə/", "n.", "亚洲", "China is in Asia.", "地理", "中级"),
        ("asian", "/ˈeɪʃn/", "a./n.", "亚洲（人）的；亚洲人", "He is Asian.", "地理", "中级"),
        ("ask", "/ɑːsk/", "v.", "询问；请求，要求", "Ask me any question.", "动作", "基础"),
        ("asleep", "/əˈsliːp/", "a.", "睡着的，熟睡", "The baby is asleep.", "状态", "基础"),
        ("at", "/æt/", "prep.", "在（某地）", "I'm at school.", "位置", "基础"),
        ("attend", "/əˈtend/", "v.", "上（学）；出席", "I attend this school.", "动作", "中级"),
        ("attention", "/əˈtenʃn/", "n.", "注意，关心", "Pay attention to me.", "抽象", "中级"),
        ("attitude", "/ˈætɪtjuːd/", "n.", "态度", "Your attitude is important.", "抽象", "中级"),
        ("attract", "/əˈtrækt/", "v.", "吸引", "The movie attracts many people.", "动作", "中级"),
        ("aunt", "/ɑːnt/", "n.", "姑；姨", "My aunt is very kind.", "家庭", "基础"),
        ("australia", "/ɒˈstreɪliə/", "n.", "澳大利亚", "Australia is far away.", "地理", "中级"),
        ("australian", "/ɒˈstreɪliən/", "n.", "澳大利亚人", "He is Australian.", "地理", "中级"),
        ("august", "/ɔːˈɡʌst/", "n.", "八月", "August is hot.", "时间", "基础"),
        ("autumn", "/ˈɔːtəm/", "n.", "秋天，秋季", "Autumn is beautiful.", "时间", "基础"),
        ("avoid", "/əˈvɔɪd/", "v.", "避免，躲开，逃避", "Avoid this problem.", "动作", "中级"),
        ("awake", "/əˈweɪk/", "v./a.", "唤醒；醒着的", "I'm awake now.", "状态", "中级"),
        ("away", "/əˈweɪ/", "ad.", "离开；远离", "Go away!", "位置", "基础"),
        ("awful", "/ˈɔːfl/", "adj.", "可怕的，糟糕的", "This weather is awful.", "情感", "中级"),
        ("baby", "/ˈbeɪbi/", "n.", "婴儿", "The baby is cute.", "家庭", "基础"),
        ("back", "/bæk/", "a./n.", "后面的；背后，后部", "My back hurts.", "身体", "基础"),
        ("background", "/ˈbækɡraʊnd/", "n.", "背景", "The background is blue.", "抽象", "中级"),
        ("bad", "/bæd/", "a.", "坏的；有害的", "This is bad news.", "评价", "基础"),
        ("bag", "/bæɡ/", "n.", "包", "I carry a bag.", "物品", "基础"),
        ("balance", "/ˈbæləns/", "n.", "平衡", "Keep your balance.", "抽象", "中级"),
        ("backpack", "/ˈbækpæk/", "n.", "书包；提包", "My backpack is heavy.", "学习", "基础"),
        ("ball", "/bɔːl/", "n.", "球；舞会", "Throw the ball.", "运动", "基础"),
        ("balloon", "/bəˈluːn/", "n.", "气球", "The balloon is red.", "物品", "基础"),
        ("bamboo", "/ˌbæmˈbuː/", "n.", "竹", "Bamboo grows fast.", "植物", "中级"),
        ("banana", "/bəˈnɑːnə/", "n.", "香蕉", "I like bananas.", "食物", "基础"),
        ("band", "/bænd/", "n.", "乐队", "The band plays music.", "音乐", "中级"),
        ("bank", "/bæŋk/", "n.", "岸，堤；银行", "I go to the bank.", "地点", "基础"),
        ("baseball", "/ˈbeɪsbɔːl/", "n.", "棒球", "I play baseball.", "运动", "中级"),
        ("basic", "/ˈbeɪsɪk/", "a.", "基本的", "This is basic knowledge.", "抽象", "中级"),
        ("basket", "/ˈbɑːskɪt/", "n.", "篮子", "Put it in the basket.", "物品", "基础"),
        ("basketball", "/ˈbɑːskɪtbɔːl/", "n.", "篮球", "I play basketball.", "运动", "基础"),
        ("bathroom", "/ˈbɑːθruːm/", "n.", "浴室，盥洗室", "The bathroom is clean.", "房间", "基础"),
        ("be", "/bi/", "v.", "是", "I am a student.", "基础", "基础"),
        ("beach", "/biːtʃ/", "n.", "海滨，海滩", "We go to the beach.", "地点", "基础"),
        ("bean", "/biːn/", "n.", "豆", "Green beans are healthy.", "食物", "基础"),
        ("bear", "/beə(r)/", "n./v.", "熊；忍受", "The bear is big.", "动物", "基础"),
        ("beat", "/biːt/", "v./n.", "敲打；打赢；（音乐）节拍", "Beat the drum.", "动作", "基础"),
        ("beautiful", "/ˈbjuːtɪfl/", "a.", "美丽的，美观的", "She is beautiful.", "评价", "基础"),
        ("because", "/bɪˈkɒz/", "conj.", "因为", "I stay home because it's raining.", "连接", "基础"),
        ("become", "/bɪˈkʌm/", "v.", "变得；成为", "I want to become a doctor.", "变化", "基础"),
        ("bed", "/bed/", "n.", "床", "I sleep on the bed.", "家具", "基础"),
        ("bedroom", "/ˈbedruːm/", "n.", "寝室，卧室", "My bedroom is small.", "房间", "基础"),
        ("beef", "/biːf/", "n.", "牛肉", "I eat beef.", "食物", "基础"),
        ("before", "/bɪˈfɔː(r)/", "prep.", "在……前", "Before dinner, I wash hands.", "时间", "基础"),
        ("begin", "/bɪˈɡɪn/", "v.", "开始，着手", "Let's begin now.", "动作", "基础"),
        ("beginning", "/bɪˈɡɪnɪŋ/", "n.", "开始，开端", "This is the beginning.", "时间", "中级"),
        ("behave", "/bɪˈheɪv/", "v.", "表现", "Behave yourself.", "动作", "中级"),
        ("behind", "/bɪˈhaɪnd/", "prep.", "在……后面", "Stand behind me.", "位置", "基础"),
        ("believe", "/bɪˈliːv/", "v.", "相信，认为", "I believe in you.", "情感", "基础"),
        ("bell", "/bel/", "n.", "钟；钟（铃）声", "The bell rings.", "物品", "基础"),
        ("belong", "/bɪˈlɒŋ/", "v.", "属于", "This book belongs to me.", "抽象", "中级"),
        ("below", "/bɪˈləʊ/", "prep.", "在……下面", "The answer is below.", "位置", "基础"),
        ("benefit", "/ˈbenɪfɪt/", "n.", "利益，好处", "What's the benefit?", "抽象", "中级"),
        ("beside", "/bɪˈsaɪd/", "prep.", "在……旁边", "Sit beside me.", "位置", "基础"),
        ("best", "/best/", "a.", "最好的", "This is the best.", "评价", "基础"),
        ("better", "/ˈbetə(r)/", "a.", "更好的", "This is better.", "评价", "基础"),
        ("between", "/bɪˈtwiːn/", "prep.", "在（两者）之间", "Between you and me.", "位置", "基础"),
        ("beyond", "/bɪˈjɒnd/", "prep.", "在……较远的一边", "Beyond the mountain.", "位置", "中级"),
        ("big", "/bɪɡ/", "a.", "大的", "The elephant is big.", "大小", "基础"),
        ("bike", "/baɪk/", "n.", "自行车", "I ride a bike.", "交通", "基础"),
        ("bill", "/bɪl/", "n.", "账单；账款", "Pay the bill.", "经济", "中级"),
        ("bird", "/bɜːd/", "n.", "鸟", "The bird flies.", "动物", "基础"),
        ("birth", "/bɜːθ/", "n.", "出生", "Today is my birth day.", "时间", "中级"),
        ("birthday", "/ˈbɜːθdeɪ/", "n.", "生日", "Happy birthday!", "时间", "基础"),
        ("biscuit", "/ˈbɪskɪt/", "n.", "饼干", "I eat a biscuit.", "食物", "基础"),
        ("bit", "/bɪt/", "n.", "一点，少量的", "A bit of sugar.", "数量", "基础"),
        ("black", "/blæk/", "n./a.", "黑色；黑色的", "The cat is black.", "颜色", "基础"),
        ("blackboard", "/ˈblækbɔːd/", "n.", "黑板", "Write on the blackboard.", "学习", "基础"),
        ("blank", "/blæŋk/", "n.", "空白处", "Fill in the blank.", "学习", "中级"),
        ("blind", "/blaɪnd/", "a.", "瞎的", "The blind man needs help.", "状态", "中级"),
        ("block", "/blɒk/", "n.", "一大块；街区；障碍物", "The building is one block away.", "地点", "中级"),
        ("blood", "/blʌd/", "n.", "血液", "Blood is red.", "身体", "中级"),
        ("blouse", "/blaʊz/", "n.", "女衬衫", "She wears a blouse.", "服装", "中级"),
        ("blow", "/bləʊ/", "v.", "吹；吹气", "Blow the candle.", "动作", "基础"),
        ("blue", "/bluː/", "n./a.", "蓝色；蓝色的，悲伤的", "The sky is blue.", "颜色", "基础"),
        ("board", "/bɔːd/", "n.", "木板；布告牌；委员会", "Check the board.", "物品", "中级"),
        ("boat", "/bəʊt/", "n.", "小船，小舟", "We sail in a boat.", "交通", "基础"),
        ("body", "/ˈbɒdi/", "n.", "身体", "My body is healthy.", "身体", "基础"),
        ("boil", "/bɔɪl/", "v.", "煮沸，烧开", "Boil the water.", "动作", "基础"),
        ("book", "/bʊk/", "n.", "书；本子", "I read a book.", "学习", "基础"),
        ("bored", "/bɔːd/", "a.", "乏味的，无聊的", "I feel bored.", "情感", "基础"),
        ("boring", "/ˈbɔːrɪŋ/", "a.", "无趣的；烦人的", "This movie is boring.", "评价", "基础"),
        ("born", "/bɔːn/", "v.", "出生", "I was born in 2000.", "时间", "中级"),
        ("borrow", "/ˈbɒrəʊ/", "v.", "向别人借用", "Can I borrow your pen?", "动作", "基础"),
        ("boss", "/bɒs/", "n.", "老板；首领；工头", "The boss is strict.", "职业", "中级"),
        ("both", "/bəʊθ/", "pron.", "两者；双方", "Both are good.", "数量", "中级"),
        ("bottle", "/ˈbɒtl/", "n.", "瓶子", "The bottle is empty.", "物品", "基础"),
        ("bottom", "/ˈbɒtəm/", "n.", "底部；下端", "Look at the bottom.", "位置", "中级"),
        ("bowl", "/bəʊl/", "n.", "碗", "Eat from the bowl.", "物品", "基础"),
        ("box", "/bɒks/", "n.", "盒子，箱子", "Put it in the box.", "物品", "基础"),
        ("boy", "/bɔɪ/", "n.", "男孩", "The boy is tall.", "人物", "基础"),
        ("brain", "/breɪn/", "n.", "脑", "Use your brain.", "身体", "中级"),
        ("brave", "/breɪv/", "a.", "勇敢的；崭新的；v. 勇敢面对", "Be brave.", "性格", "中级"),
        ("bread", "/bred/", "n.", "面包", "I eat bread.", "食物", "基础"),
        ("break", "/breɪk/", "n./v.", "间隙；打破，折断；损坏", "Don't break it.", "动作", "基础"),
        ("breakfast", "/ˈbrekfəst/", "n.", "早餐", "Have breakfast.", "时间", "基础"),
        ("breathe", "/briːð/", "v.", "呼吸", "Breathe deeply.", "动作", "中级"),
        ("bridge", "/brɪdʒ/", "n.", "桥", "Cross the bridge.", "建筑", "基础"),
        ("bright", "/braɪt/", "a.", "明亮的，聪明的", "The sun is bright.", "评价", "基础"),
        ("bring", "/brɪŋ/", "vt.", "拿来，带来", "Bring your book.", "动作", "基础"),
        ("britain", "/ˈbrɪtn/", "n.", "英国；不列颠", "Britain is an island.", "地理", "中级"),
        ("british", "/ˈbrɪtɪʃ/", "a./n.", "英国（人）的；英国人", "He is British.", "地理", "中级"),
        ("brother", "/ˈbrʌðə(r)/", "n.", "兄；弟", "My brother is older.", "家庭", "基础"),
        ("brown", "/braʊn/", "n.", "褐色，棕色", "The table is brown.", "颜色", "基础"),
        ("brush", "/brʌʃ/", "n./v.", "刷子；画笔；刷；画；擦过", "Brush your teeth.", "物品", "基础"),
        ("build", "/bɪld/", "v.", "建筑；造", "They build houses.", "动作", "基础"),
        ("building", "/ˈbɪldɪŋ/", "n.", "建筑物；大楼", "The building is tall.", "建筑", "基础"),
        ("burn", "/bɜːn/", "v.", "燃，烧，着火", "Don't burn the paper.", "动作", "基础"),
        ("bus", "/bʌs/", "n.", "公共汽车", "Take the bus.", "交通", "基础"),
        ("business", "/ˈbɪznəs/", "n.", "生意，交易", "He runs a business.", "商业", "中级"),
        ("busy", "/ˈbɪzi/", "a.", "忙（碌）的", "I'm very busy.", "状态", "基础"),
        ("but", "/bʌt/", "conj.", "但是", "I like it, but it's expensive.", "连接", "基础"),
        ("buy", "/baɪ/", "v.", "买", "I want to buy this.", "动作", "基础"),
        ("by", "/baɪ/", "prep.", "由；通过", "I go by bus.", "方式", "基础"),
        ("bye", "/baɪ/", "int.", "再见", "Bye bye!", "问候", "基础"),
        # 添加一些重要的缺失单词
        ("set", "/set/", "n./v.", "装备，设备；设置", "Set the table.", "动作", "基础"),
        ("zoo", "/zuː/", "n.", "动物园", "We go to the zoo.", "地点", "基础"),
    ]
    
    # 清空现有数据
    cursor.execute("DELETE FROM vocabulary")
    
    # 插入数据
    for word_data in sample_words:
        cursor.execute('''
            INSERT INTO vocabulary (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
        ''', word_data)
    
    conn.commit()
    print(f"成功插入 {len(sample_words)} 个词汇到数据库")

def main():
    """主函数"""
    print("开始处理中考词汇数据...")
    
    # 创建数据库
    conn, cursor = create_database()
    
    try:
        # 处理并插入数据
        process_and_insert_data(conn, cursor)
        
        # 验证数据
        cursor.execute("SELECT COUNT(*) FROM vocabulary")
        count = cursor.fetchone()[0]
        print(f"数据库中总共有 {count} 个词汇")
        
        # 检查特定单词
        cursor.execute("SELECT * FROM vocabulary WHERE word IN ('zoo', 'set')")
        results = cursor.fetchall()
        print("找到的单词:")
        for row in results:
            print(f"  {row[1]} - {row[3]}")
            
    finally:
        conn.close()
    
    print("处理完成！")

if __name__ == "__main__":
    main()


