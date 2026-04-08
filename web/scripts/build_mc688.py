#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Build web/data/mc688_21day.json — 中考英语单项选择核心688词，21天计划。"""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "data" / "mc688_21day.json"


def rank_to_day(rank: int) -> int:
    if rank >= 661:
        return 21
    return (rank - 1) // 33 + 1


def parse_tsv_block(block: str) -> list[tuple[int, str, str]]:
    rows: list[tuple[int, str, str]] = []
    for line in block.strip().splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        parts = line.split("\t")
        if len(parts) < 3:
            continue
        r = int(parts[0].strip())
        w = parts[1].strip()
        m = parts[2].strip()
        rows.append((r, w, m))
    return rows


# Day1–3：英文自 OCR 校对，中文为简明释义
BLOCK_1_99 = """
1	value	n. 价值；重要性 v. 重视
2	wear	v. 穿；戴；磨损 n. 穿着；耐用
3	different	adj. 不同的；各种的
4	write	v. 写；写作；写信
5	important	adj. 重要的；重大的
6	plant	n. 植物；工厂 v. 种植
7	poem	n. 诗；韵文
8	law	n. 法律；法规；定律
9	worry	v. 担心；烦恼 n. 忧虑
10	wait	v. 等待；等候 n. 等待
11	international	adj. 国际的
12	follow	v. 跟随；遵循；听懂
13	idea	n. 主意；想法；概念
14	enjoy	v. 享受；喜爱
15	planet	n. 行星
16	subject	n. 学科；主题；主语
17	side	n. 边；侧面；一方
18	hear	v. 听见；听说
19	hand	n. 手；指针 v. 递；交
20	meanwhile	adv. 与此同时
21	treat	v. 对待；治疗；请客 n. 款待
22	busy	adj. 忙碌的；热闹的
23	hope	n. 希望 v. 希望；期望
24	accord	v. 一致；符合 n. 一致
25	probably	adv. 很可能；大概
26	touch	v. 触摸；感动 n. 接触；一点
27	spring	n. 春天；弹簧 v. 跳；涌现
28	collect	v. 收集；领取
29	health	n. 健康；卫生
30	paint	v. 绘画；油漆 n. 油漆；颜料
31	please	int. 请 v. 使高兴；请
32	volunteer	n. 志愿者 v. 自愿做
33	decide	v. 决定；下决心
34	build	v. 建造；创建 n. 体格
35	throw	v. 扔；抛
36	stick	n. 棍；手杖 v. 粘贴；刺
37	wild	adj. 野生的；狂热的 n. 荒野
38	yet	adv. 还；已经 conj. 然而
39	museum	n. 博物馆
40	another	adj. 又一；不同的 pron. 另一个
41	during	prep. 在……期间
42	finish	v. 完成；结束 n. 结局
43	last	adj. 最后的 v. 持续 adv. 上次
44	speed	n. 速度 v. 加速
45	enough	adv. 足够地 adj. 足够的 pron. 足够
46	care	v. 关心；在乎 n. 照料；小心
47	yard	n. 院子；码
48	tie	n. 领带；联系 v. 系；平局
49	waste	n. 浪费；废物 v. 浪费 adj. 荒芜的
50	famous	adj. 著名的
51	suggest	v. 建议；暗示；表明
52	help	v. 帮助 n. 帮助；帮手
53	believe	v. 相信；认为
54	power	n. 力量；电力；权力
55	excite	v. 使兴奋；激发
56	exam	n. 考试；检查
57	spirit	n. 精神；心灵；情绪
58	happen	v. 发生；碰巧
59	group	n. 组；群 v. 分组
60	legend	n. 传说；传奇人物
61	colour	n. 颜色 v. 给……着色
62	survive	v. 幸存；比……活得长
63	require	v. 需要；要求
64	activity	n. 活动；活跃
65	search	n. 搜索 v. 搜寻；搜查
66	transport	n. 运输 v. 运输
67	choose	v. 选择；挑选
68	scare	v. 惊吓 n. 恐慌
69	dream	n. 梦；梦想 v. 做梦；梦想
70	cause	n. 原因；事业 v. 导致；使发生
71	earth	n. 地球；泥土；陆地
72	example	n. 例子；榜样
73	danger	n. 危险；威胁
74	produce	v. 生产；引起 n. 农产品
75	afraid	adj. 害怕的；担心的
76	information	n. 信息；消息；资料
77	prove	v. 证明；结果是
78	wealthy	adj. 富有的；丰富的
79	course	n. 课程；过程；一道菜
80	birth	n. 出生；起源
81	close	v. 关闭 adj. 接近的；亲密的
82	national	adj. 国家的；民族的 n. 国民
83	percent	n. 百分之…… adj. 每一百中的
84	act	v. 行动；表演 n. 行为；法令
85	monitor	n. 班长；显示器 v. 监控
86	level	n. 水平；等级 adj. 平的
87	resume	v. 重新开始 n. 简历
88	experience	n. 经验；经历 v. 经历；体验
89	otherwise	adv. 否则；另外
90	tolerance	n. 容忍；公差
91	bring	v. 带来；引起
92	project	n. 项目；课题 v. 投射；规划
93	website	n. 网站
94	exercise	n. 锻炼；练习 v. 锻炼；行使
95	necessary	adj. 必要的；必然的
96	advice	n. 建议；忠告
97	heart	n. 心；内心；中心
98	company	n. 公司；陪伴；同伴
99	grow	v. 生长；种植；变得
"""

# Day5–6：英文自 OCR，中文简明
BLOCK_133_198 = """
133	recent	adj. 最近的；新近的
134	trade	n. 贸易；行业 v. 交易
135	goal	n. 目标；进球得分
136	include	v. 包括；列入
137	show	v. 展示；表明 n. 演出；节目
138	spare	adj. 空闲的；备用的 v. 抽出；饶恕
139	dining	n. 就餐；餐厅（dining room 常考）
140	festival	n. 节日；庆典
141	operate	v. 操作；动手术；经营
142	review	v. 复习；回顾 n. 评论；审查
143	area	n. 地区；面积；领域
144	desert	n. 沙漠 v. 抛弃
145	smart	adj. 聪明的；时髦的
146	smoke	n. 烟 v. 吸烟；冒烟
147	hurt	v. 伤害；疼痛 adj. 受伤的
148	agree	v. 同意；一致
149	prefer	v. 更喜欢；宁愿
150	natural	adj. 自然的；天生的
151	upset	adj. 心烦的；不适的 v. 使心烦
152	program	n. 节目；程序；计划 v. 编程
153	toilet	n. 厕所；盥洗室
154	warn	v. 警告；提醒
155	invite	v. 邀请；招致
156	join	v. 加入；参加；连接
157	member	n. 成员；会员
158	progress	n. 进步 v. 前进；进展
159	catch	v. 抓住；赶上；感染
160	hold	v. 握住；举行；容纳
161	reason	n. 原因；理由 v. 推理
162	die	v. 死亡；消失
163	novel	n. 小说 adj. 新颖的
164	receive	v. 收到；接待
165	save	v. 拯救；节省；储存
166	chance	n. 机会；可能性 v. 碰巧
167	club	n. 俱乐部；棍棒
168	competition	n. 比赛；竞争
169	stress	n. 压力；重音 v. 强调
170	dragon	n. 龙
171	encourage	v. 鼓励；促进
172	shake	v. 摇动；握手 n. 摇动
173	supply	n. 供应；补给 v. 供应
174	accident	n. 事故；意外
175	communicate	v. 交流；传达
176	culture	n. 文化；栽培
177	either	pron.（两者中）任一 adv. 也（用于否定）
178	space	n. 空间；太空；空格
179	temple	n. 庙宇；太阳穴
180	bored	adj. 无聊的；厌烦的
181	grade	n. 年级；成绩；等级 v. 评分
182	public	adj. 公共的；公开的 n. 公众
183	common	adj. 常见的；共同的
184	knife	n. 刀
185	leave	v. 离开；留下 n. 假期
186	person	n. 人；人称
187	translate	v. 翻译
188	count	v. 数数；认为 n. 计数
189	pay	v. 支付；付出 n. 工资
190	climb	v. 爬；攀登
191	control	v. 控制；管理 n. 控制；操纵装置
192	line	n. 线；排；台词 v. 排队；画线
193	scene	n. 场面；景色；现场
194	sport	n. 运动；体育
195	truth	n. 真相；事实
196	honest	adj. 诚实的；坦率的
197	expensive	adj. 昂贵的
198	hardly	adv. 几乎不；刚刚
"""

# 100–132、199–688：用户截图转写（节选合并）
BLOCK_100_688 = """
100	between	prep. 在……之间
101	set	v. 放置，安置 n. 一套；一副
102	study	v. 研究；学习 n. 书房；研究；学习
103	position	n. 位置；地位 v. 放置
104	able	adj. 能做到……的；可以……的
105	interview	v. 采访；面试 n. 会见；面试；采访
106	angry	adj. 生气的；愤怒的
107	develop	v. 发展；养成；开发；冲洗（胶片）
108	farm	n. 农场；农田；耕种
109	gift	n. 礼物；天赋
110	mouse	n. 老鼠；鼠标
111	allow	v. 允许；准许
112	remind	v. 使想起，使记起；提醒
113	joke	n. 笑话；玩笑 v. 开玩笑
114	environment	n. 环境；外界
115	serve	v. 服务；招待
116	through	prep. 穿过
117	weather	n. 天气；气象
118	mean	v. 意思是；打算 adj. 吝啬的
119	symbol	n. 象征；符号；标志
120	forest	n. 森林；丛林
121	teenage	adj. 青少年的；十几岁的
122	head	n. 头部；领袖 adj. 首要的； v. 前进
123	tower	n. 塔；高楼 v. 高耸；屹立；超越
124	though	conj. 虽然；尽管；然而；不过
125	create	v. 创造；创作；产生
126	complete	adj. 完整的 v. 完成
127	knock	v. 敲击；互撞 n. 敲门声；敲击声
128	population	n. 人口；居民
129	educate	v. 教育；训练；培养
130	heavy	adj. 沉重的；巨大的
131	college	n. 学院；大学
132	obey	v. 服从；遵守
199	neighbourhood	n. 居民区；街区
200	print	v. 打印 n. 印刷；印记
201	theatre	n. 剧场；电影院
202	conditional	adj. 有条件的
203	deal	v. 处理 n. 大量；协议
204	nervous	adj. 神经质的；紧张的；焦虑的
205	purpose	n. 目的
206	shape	n. 形状；外形 v. 使成为……形状
207	arm	n. 手臂；袖子 v. 武装
208	attention	n. 注意；兴趣
209	break	v. 弄坏；违犯；打断 n. 休息
210	certain	adj. 确实的；确定的
211	polite	adj. 有礼貌的；客套的
212	alone	adj. 独自；寂寞 adv. 独自
213	method	n. 方法；条理
214	skate	v. 滑冰
215	instead	adv. 代替；反而
216	memory	n. 记忆力；记忆
217	special	adj. 特别的；专用的
218	celebrate	v. 庆祝；赞美
219	team	n. 队，组 v. 使合作
220	continue	v. 持续；持续做
221	active	adj. 活跃的
222	century	n. 世纪；百年
223	cost	n. 费用 v. 需付费
224	discuss	v. 讨论；论述
225	across	adv. 横过；在对面 prep. 从一边到另一边
226	government	n. 政府；政体
227	pretend	v. 假装；装扮 adj. 假装的
228	society	n. 社会；社团
229	above	prep. 在……上面 adv. 在上面 adj. 上述的
230	record	n. 记录；唱片 v. 记录；录制
231	station	n. 火车站；长途汽车站
232	excuse	n. 借口；理由 v. 原谅
233	tire	v. 使疲劳，疲倦 n. 轮胎
234	fail	v. 失败；未做 n. 不及格
235	human	n. 人 adj. 人本性的；有人情味的
236	mind	v. 介意 n. 头脑
237	order	v. 命令；定购 n. 顺序；条理；命令；秩序
238	silence	n. 寂静；沉默
239	introduce	v. 把……介绍（给）
240	plastic	n. 塑料 adj. 塑料的；整形的
241	proper	adj. 恰当的；正确的
242	sick	adj. 生病的
243	thought	n. 看法；思想
244	cheap	adj. 便宜的
245	guide	n. 指南；导游 v. 指引；指导
246	list	n. 清单；目录 v. 列……清单
247	process	n. 过程 v. 加工
248	ski	n. 滑雪板 v. 滑雪
249	train	n. 火车 v. 培训，训练
250	borrow	v. 借；引用
251	express	v. 表达 n. 快递服务
252	fight	v. 打仗；打架
253	advantage	n. 优势；有利于
254	electric	adj. 电的 n. 电
255	confident	adj. 自信的；确信的
256	language	n. 语言
257	expect	v. 预期；期望
258	explain	v. 解释；说明
259	realize	v. 意识到；实现
260	respect	n./v. 尊敬
261	sightseeing	n. 观光；游览
262	stamp	n. 邮票 v. 盖章；重踩
263	spell	v. 拼写
264	beside	prep. 在……旁边
265	among	prep. 在……中
266	brain	n. 大脑；智力；聪明的人
267	consider	v. 仔细考虑；认为
268	manage	v. 管理；控制
269	material	n. 材料；原料 adj. 物质的；重要的
270	retire	v. 退休；（因伤）退出（比赛等）
271	sting	v. 叮；敲诈 n. 刺；叮伤
272	accept	v. 接受；同意
273	direct	adj. 直接的；笔直的 v. 导演；指路
274	own	adj. 自己的 v. 拥有
275	provide	v. 提供；给予
276	suffer	v.（因疾病、痛苦、悲伤等）受苦；遭受；忍受
277	bank	n. 银行；岸 v. 把（钱）存入银行；开账户
278	butterfly	n. 蝴蝶；蝶泳
279	check	n./v. 检查
280	jungle	n.（热带）丛林
281	president	n. 总统；主席
282	chocolate	n. 巧克力
283	energy	n. 精力；能源
284	form	n. 表格；形式 v. 形成；建立
285	cartoon	n. 动画片；漫画
286	mention	v. 提到；写到 n. 提及
287	present	adj. 当前的；出席的 n. 礼物；目前 v. 提出；呈现
288	reflect	v. 反映；反射；表明
289	state	n. 状态；国家；州 v. 陈述；公布
290	imagine	v. 想象；认为
291	attract	v. 吸引；引起
292	breath	n. 呼吸
293	choice	n. 选择 adj. 优选的
294	delicious	adj. 美味的；令人愉快的
295	design	n. 设计；安排 v. 设计；计划
296	invent	v. 发明；创造
297	prevent	v. 阻止；阻碍
298	appear	v. 显得；似乎；出现
299	describe	v. 描述；形容
300	field	n. 田地；领域
301	land	n. 陆地；国家 v. 降落
302	mark	v. 做标记；评分 n. 记号；成绩
303	amaze	v. 使惊奇；使惊愕
304	popular	adj. 受欢迎的；普遍的
305	regret	v. 后悔；感到遗憾 n. 懊悔；遗憾
306	temperature	n. 气温；体温
307	against	prep. 反对；违反；倚靠
308	hunt	n./v. 打猎；搜寻
309	Internet	n. 互联网
310	notice	n. 通知；公告牌 v. 注意到
311	comfortable	adj. 舒适的
312	hate	v. 讨厌；憎恨 n. 厌恶；所憎恨的人（或事物）
313	perform	v. 执行；演出；运转
314	reach	v. 到达；伸手；够得着
315	stay	v. 停留；保持；暂住 n. 停留；逗留
316	straight	adv. 笔直地；径直；直截了当地 adj. 直的；直率的
317	various	adj. 各种各样的；多姿多彩的
318	bright	adj. 明亮的；聪明的
319	discover	v. 发现；查明
320	risk	n. 危险；风险 v. 冒险做
321	burn	v. 燃烧；发烫 n. 烧伤；烫伤
322	reduce	v. 减少；降低
323	smooth	adj. 光滑的；顺利的 v. 使光滑
324	achieve	v. 达到；完成
325	centre	n. 中心；中心区
326	survey	n./v. 调查；勘测
327	magazine	n. 杂志；期刊
328	artist	n. 艺术家；画家
329	blood	n. 血；血统
330	heat	n. 热；温度 v. 加热；（使）变暖
331	shout	v. 大声说；喊叫 n. 呼喊；喊叫声
332	tap	v. 轻敲 n. 水龙头；轻拍
333	article	n. 文章；物品；冠词
334	satisfy	v. 使满意；使确信
335	trust	v. 信任；希望 n. 信任；委托
336	beach	n. 海滩 v. 上岸
337	daily	adj. 日常的；按日的 n. 日报
338	dirty	adj. 肮脏的；卑鄙的 v. 弄脏；使变脏
339	master	n. 主人；大师；硕士 v. 掌握；控制
340	represent	v. 代表；展示
341	serious	adj. 严重的；严肃的；认真的
342	soil	n. 土壤；国土
343	adult	n. 成年人 adj. 成年的；成熟的
344	concert	n. 音乐会；演奏会
345	eagle	n. 鹰；雕
346	hole	n. 洞；裂口 v. 打洞
347	place	n. 位置；地位 v. 放置；安顿
348	sentence	n. 句子；判决 v. 判刑
349	while	conj. 当……的时候；然而 n. 一段时间；一会儿
350	magic	n. 魔法；巫术 adj. 有魔力的；神奇的
351	brave	adj. 勇敢的；崭新的 v. 勇敢面对
352	camp	n. 营地；度假营；阵营 v. 宿营；野营
353	cheer	n. 欢呼声；欢乐的气氛 v. 欢呼；鼓励
354	event	n. 大事件；（体育运动的）比赛项目
355	expert	n. 专家；能手 adj. 熟练的；内行的
356	hide	v. 藏；隐瞒 n. 藏身处
357	seed	n. 种子；种子选手 v. 结籽
358	size	n. 大小；尺码
359	vegetable	n. 蔬菜；植物人
360	depend	v. 依靠；信任
361	avoid	v. 避免；回避
362	behaviour	n. 行为；态度；表现方式
363	fill	v.（使）充满；填补；满足
364	affect	v. 影响；（感情上）深深打动
365	poison	n. 毒药 v. 毒害
366	responsible	adj. 有责任心的；承担责任的
367	underground	adj. 地下的；秘密的 adv. 在地下 n. 地铁
368	actually	adv. 事实上；竟然
369	bridge	n. 桥；纽带；桥牌 v. 造桥
370	capital	n. 首都；省会；大写字母；资本 adj. 大写的
371	customer	n. 顾客
372	earthquake	n. 地震
373	influence	n. 影响；控制力 v. 影响；对……起作用
374	knowledge	n. 知识；学问
375	message	n. 信息
376	move	v. 移动；感动 n. 行动；搬迁
377	protect	v. 保护
378	raise	v. 筹集；举起；增加
379	wonder	v. 想知道 n. 好奇；奇迹
380	ability	n. 能力；才智
381	fantastic	adj. 极好的
382	peace	n. 和平；平静
383	sorrow	n. 悲伤 v. 感到（或表示）悲伤
384	succeed	v. 成功；继承
385	community	n. 社区；团体；社团
386	lie	v. 说谎；躺下；位于 n. 谎言
387	plenty	pron. 大量；众多 adv. 大量；非常 n. 富足；充裕
388	possible	adj. 可能的；合理的
389	foreign	adj. 外国的；外交的
390	patience	n. 耐心；毅力
391	refer	v. 提到；参考
392	strong	adj. 强壮的；强烈的
393	beat	v. 打败；敲打；有规律地作响
394	chemistry	n. 化学；化学性质
395	conversation	n.（非正式）谈话；交谈
396	dry	adj. 干的；枯燥乏味的 v. 使变干；弄干
397	habit	n. 习惯
398	hurry	v. 匆忙；催促 n. 匆忙；赶快
399	matter	n. 问题；事件 v. 要紧；有重大影响
400	relax	v. 放松；休息
401	calm	adj. 镇静的；沉着的 v. 使平静；使镇定
402	character	n. 性格；特点；角色
403	crowd	n. 人群；观众 v. 拥挤；挤满
404	pride	n. 骄傲；自豪
405	share	v. 分享；分担
406	young	adj. 年轻的 n. 年轻人
407	attitude	n. 态度；看法
408	deadline	n. 截止日期
409	income	n. 收入；所得
410	increase	v. 增加；加大 n. 增长；提高
411	likely	adj. 很可能的 adv. 很可能；或许
412	spend	v. 度过；花费 n. 预算
413	title	n. 标题；职称；称谓 v. 加标题于
414	bottom	n. 底部；末端 adj. 底部的
415	comedy	n. 喜剧
416	restaurant	n. 餐馆
417	wise	adj. 明智的
418	machine	n. 机械，机器
419	address	n. 地址；演讲
420	excellent	adj. 卓越的；极好的
421	host	n. 主人；主持人 v. 主持；当主人招待
422	intelligence	n. 智力
423	joy	n. 欢乐；高兴 v. 高兴；使快乐
424	simple	adj. 简单的；单纯的
425	square	n. 广场；平方；正方形 adj. 平方的；正方的
426	disappear	v. 消失；失踪
427	distance	n. 距离；远方
428	lay	v. 下（蛋）；放置
429	opposite	prep. 在……的对面 adj. 相反的 n. 对立面
430	publish	v. 出版；发表
431	strict	adj. 严格的
432	term	n. 学期；术语
433	blind	adj. 盲目的；瞎的
434	complain	v. 抱怨；控诉
435	convenience	n. 便利；（英）公共厕所
436	credit	n. 信用；信誉
437	harmful	adj. 有害的
438	leader	n. 领导者；指挥者
439	prize	n. 奖品；奖赏
440	speech	n. 演讲；讲话
441	ancient	adj. 古代的；古老的
442	belt	n. 带；腰带 v. 用带子系住
443	bet	n./v. 打赌
444	post	n. 邮件；岗位 v. 邮递；公布
445	proud	adj. 自豪的；得意的
446	return	v. 返回；报答 n. 返回；归还
447	silly	adj. 愚蠢的 n. 傻瓜
448	sort	n. 种类 v. 将……分类
449	bath	n. 沐浴 v. 洗澡
450	circle	n. 循环；圆；圈子 v. 划圈
451	correct	adj. 正确的 v. 改正
452	date	n. 日期；约会 v. 和……约会
453	except	prep. 除……之外
454	experiment	n. 实验；尝试 v. 尝试；进行试验
455	flight	n. 飞行；班机
456	hometown	n. 家乡；故乡
457	improve	v. 改善；增加
458	practice	v./n. 实践；练习
459	saying	n. 话；谚语
460	addict	v. 使沉溺；使上瘾 n. 有瘾的人；入迷的人
461	belief	n. 相信；信赖；信仰
462	immediately	adv. 立即；直接地
463	science	n. 科学；理科
464	structure	n. 结构；构造
465	chat	n./v. 聊天；闲谈
466	death	n. 死；死亡
467	disable	v. 使失去能力；使残废
468	disappoint	v. 使失望
469	habitat	n. 栖息地；产地
470	price	n. 价格；代价
471	promise	n./v. 许诺；允诺
472	relative	n. 亲戚；相关物 adj. 相对的；有关系的
473	sunshine	n. 阳光
474	terrible	adj. 可怕的；糟糕的
475	thankful	adj. 感谢的；欣慰的
476	cheat	v. 欺骗；作弊 n. 作弊；骗子
477	surf	v. 冲浪；浏览 n. 海浪
478	argue	v. 争论；辩论
479	average	n. 平均；平均数 adj. 平均的；普通的
480	season	n. 季节；季
481	talent	n. 才能；天才
482	task	n. 任务
483	asleep	adj. 睡着的 adv. 熟睡地
484	compare	v. 相比；比较 n. 比较
485	damage	n./v. 损害；损毁
486	disease	n. 病；弊病
487	hobby	n. 嗜好；业余爱好
488	journey	n. 旅行；行程 v. 旅行
489	point	n. 要点；得分 v. 指向
490	regard	v. 考虑；看待；把……看作
491	stand	v. 站立；忍受
492	treasure	n. 财富；财宝 v. 珍爱；珍藏
493	work	v. 工作 n. 作品；著作
494	abroad	adv. 在国外；到海外 adj. 往国外的
495	connect	v. 连接；连结；联合
496	drug	n. 药；毒品
497	exchange	n./v. 交换；兑换
498	medicine	n. 药；医学
499	opinion	n. 意见；主张
500	report	n. 报告；报道；成绩单 v. 报告
501	rule	n. 规则 v. 统治；规定
502	soldier	n. 军人
503	strength	n. 力量；力气
504	way	n. 方法；道路
505	wide	adj. 广泛的；宽的
506	afford	v. 买得起
507	champion	n. 冠军 v. 为……而斗争
508	destroy	v. 破坏；毁坏
509	height	n. 高度；身高
510	illness	n. 疾病；病
511	similar	adj. 相似的 n. 类似物
512	skin	n. 皮肤
513	smell	v. 嗅，闻 n. 气味，嗅觉
514	stomach	n. 胃；腹部
515	travel	v. 旅行 n. 旅行；游历
516	add	v. 增加；计算……总和
517	ahead	adj. 在前的；领先的 adv. 向前地；提前地
518	attend	v. 出席；上（大学等）；照料
519	belong	v. 属于
520	coach	n. 教练；长途汽车 v. 训练；指导
521	cute	adj. 可爱的；漂亮的
522	engineer	n. 工程师 v. 密谋策划；设计
523	instructor	n. 指导者；讲师
524	mad	adj. 疯狂的；着迷的
525	province	n. 省；领域
526	soft	adj. 软的；温柔的
527	together	adv. 一起；总共
528	audience	n. 观众；听众；读者
529	awful	adj. 很坏的；极讨厌的
530	basic	adj. 基本的；初级的
531	bury	v. 埋葬
532	challenge	n. 挑战；质疑 v. 向（某人）挑战；质疑
533	contact	n./v. 联系；接触
534	couple	n. 一对；两个人
535	crazy	adj. 疯狂的；生气的
536	doubt	n. 疑惑 v. 怀疑
537	exhibition	n. 展览，展览品；表演，表现
538	manner	n. 方法；礼貌；礼仪
539	marry	v. 结婚；嫁；娶
540	million	n. 一百万；大量
541	pollute	v. 污染，弄脏
542	research	n. 研究；调查
543	skill	n. 技巧；本领
544	university	n.（综合性）大学
545	vacation	n. 假期
546	associate	v. 联系；（与……）混在一起
547	cancer	n. 癌症；（社会）毒瘤
548	church	n. 教堂；礼拜（仪式）
549	climate	n. 气候
550	digital	adj. 数码的，数字式的
551	enter	v. 进来；开始从事
552	graduate	v. 毕业，获得（学位） n. 毕业生
553	interest	n. 兴趣 v. 使感兴趣
554	lesson	n. 一节课；经验；教训
555	mountain	n. 高山；山脉
556	ocean	n. 海洋
557	stop	v. 停止；阻止 n. 公交车站
558	strange	adj. 奇怪的；陌生的
559	balance	n. 平衡；均势
560	bow	v. 鞠躬；点头；（使）弯曲 n. 鞠躬；弯腰行礼
561	career	n. 职业；事业；生涯
562	deliver	v. 递送；传送；发布
563	inform	v. 通知；通告
564	painful	adj. 令人疼痛的；令人痛苦的
565	private	adj. 私有的，私人的
566	regular	adj. 规则的，有规律的；频繁的
567	relationship	n. 关系；联系
568	rescue	v. 营救 n. 获救，救援
569	aim	n. 目标，目的 v. 目的是
570	apply	v. 申请；应用
571	cure	v. 治愈 n. 治疗
572	judge	v. 判断，断定 n. 法官
573	monster	n. 怪物，怪兽；庞然大物
574	organization	n. 组织；团体；机构
575	race	n. 赛跑，速度竞赛；竞争
576	solve	v. 解决，处理
577	battery	n. 电池；一群
578	blow	v. 吹；刮动；吹奏
579	board	n. 板，木板 v. 上船（或火车、飞机、汽车）
580	candle	n. 蜡烛
581	conclusion	n. 结论，推论；结果
582	infer	v. 推断，推论
583	resource	n. 资源；财力；资料
584	step	n. 步骤；台阶 v. 踩，行走
585	brush	n. 刷子；灌木丛 v. 刷净
586	congratulation	n. 祝贺；贺词
587	detail	n. 细节；琐事；详情
588	effort	n. 努力；尽力；费力的事
589	embarrass	v. 使尴尬；使窘迫；使陷入困境
590	lazy	adj. 懒惰的；马虎的
591	praise	v. 赞扬；称赞 n. 赞美
592	prepare	v. 准备
593	style	n. 方式；款式
594	support	v. 支持；鼓励 n. 支撑；赞助
595	uniform	n. 制服；校服
596	apologize	v. 道歉
597	band	n. 乐队；带子
598	burden	n.（义务、责任等的）重担；负担
599	ceremony	n. 典礼；仪式
600	coast	n. 海岸；海滨
601	condition	n. 状况，状态；环境，条件
602	desperate	adj. 不顾一切的；绝望的
603	display	n./v. 陈列；展览
604	harvest	n. 收割；收获
605	result	n. 结果，后果 v. 导致
606	spread	n./v. 传播；扩展
607	store	v. 储存，贮藏 n. 商店；店铺
608	type	n. 类型；特征 v. 打字
609	alive	adj. 活着的
610	annoy	v. 打扰；惹恼
611	beyond	prep. 超过；越过 adv. 在远处
612	bush	n. 灌木丛
613	classical	adj. 古典的；经典的
614	contest	v. 竞争；争辩 n. 比赛
615	depress	v. 使沮丧，使丧气
616	earn	v. 获得，挣得；赚得
617	equip	v. 装备；配备；提供
618	honor	n. 荣誉 v. 给……以荣誉；尊敬
619	suppose	v. 假定；猜想；推测
620	taste	v. 尝起来 n. 味道，口味
621	traffic	n. 交通；贸易；运输
622	weigh	v. 称……的重量；权衡；考虑
623	astronaut	n. 宇航员；太空人
624	decorate	v. 装饰；布置
625	dish	n. 碗，盘；一道菜
626	double	adj. 双倍的；双重的 n. 两倍；双份
627	entertainment	n. 娱乐；文娱节目
628	profession	n. 职业；同行
629	score	n. 分数，得分 v. 得分
630	sound	v. 听起来 n. 声音
631	underline	v. 在 …… 下画线；强调
632	aid	n./v. 帮助；援助
633	amount	n. 数量；金额 v. 总计；合计
634	amusement	n. 娱乐活动；可笑，愉悦
635	blame	v. 责备 n. 责备；坏事或错事的责任
636	case	n. 情况；实例；箱
637	cash	n. 现金
638	download	v. 下载
639	refuse	v. 拒绝
640	reply	n./v. 回复，答复
641	roommate	n. 室友
642	sweep	v. 扫除；打扫
643	technology	n. 科技；工业技术
644	tourist	n. 旅行者；观光者
645	admire	v. 钦佩；赞美
646	calendar	n. 日历，日程表
647	confuse	v. 使混乱；使困惑
648	dare	v. 挑战；不惧；胆敢
649	digest	v. 消化；领悟；理解
650	discourage	v. 使气馁，使沮丧
651	injury	n. 伤害；受伤处
652	match	v. 使相配 n. 比赛；火柴
653	repair	v. 修复，恢复；修理
654	trouble	n./v. 麻烦，烦恼
655	award	v. 授予 n. 奖品
656	charge	v. 充电；控诉 n. 责任；费用
657	curious	adj. 好奇的
658	differ	v. 使……不同
659	hang	v. 悬，挂；垂下
660	harbour	n. 海港
661	anxious	adj. 焦虑的，忧虑的；渴望的
662	appreciate	v. 欣赏；重视；感激
663	arrangement	n. 安排；筹备；约定
664	background	n. 背景
665	visit	n./v. 访问；拜访；参观
666	aloud	adv. 出声地；大声地
667	benefit	n. 优势；益处 v. 使受益
668	composition	n. 作文；（音乐、艺术、诗歌）作品
669	concentrate	v. 集中（注意力），聚精会神；使……集中
670	escape	v. 逃跑；摆脱 n. 逃脱；逃避
671	humorous	adj. 滑稽有趣的；有幽默感的
672	remain	v. 仍然是；剩余；遗留
673	system	n.（思想或理论）体系，制度；系统
674	shame	n. 羞耻，羞愧；遗憾
675	available	adj. 可获得的，可用的；有空的
676	command	n. 命令 v. 命令；指挥
677	degree	n.（温度、角度）度数；程度；学位
678	dig	v. 挖；掘（地）
679	disaster	n. 灾难；灾祸
680	main	adj. 主要的；最重要的
681	note	n. 笔记；纸币
682	traditional	adj. 传统的；习俗的
683	use	v. 使用；利用 n. 使用
684	impress	v. 给……留下深刻的印象；使钦佩
685	insist	v. 坚决要求；坚持
686	positive	adj. 自信的；积极乐观的
687	surprise	n. 惊奇，惊讶 v. 使惊奇；使诧异
688	switch	n.（电路的）开关 v.（使）改变，转变
"""


def main() -> None:
    merged: dict[int, tuple[str, str]] = {}
    for block in (BLOCK_1_99, BLOCK_133_198, BLOCK_100_688):
        for r, w, m in parse_tsv_block(block):
            merged[r] = (w, m)

    ranks = sorted(merged)
    if len(merged) != 688 or ranks[0] != 1 or ranks[-1] != 688:
        missing = [i for i in range(1, 689) if i not in merged]
        raise SystemExit(f"expected 688 entries 1..688, got {len(merged)}, missing sample: {missing[:20]}")

    entries = []
    days_out = []
    for d in range(1, 22):
        day_entries = []
        for r in range(1, 689):
            if rank_to_day(r) != d:
                continue
            w, m = merged[r]
            day_entries.append({"rank": r, "word": w, "meaning": m})
            entries.append({"rank": r, "day": d, "word": w, "meaning": m})
        days_out.append({"day": d, "count": len(day_entries), "entries": day_entries})

    doc = {
        "version": 1,
        "label": "中考英语【单项选择】核心高频688词",
        "subtitle": "21天背诵计划（每天约33词，第21天28词）",
        "days": days_out,
        "entries": entries,
    }
    OUT.write_text(json.dumps(doc, ensure_ascii=False, indent=2), encoding="utf-8")
    print("written", OUT, "entries", len(entries))


if __name__ == "__main__":
    main()
