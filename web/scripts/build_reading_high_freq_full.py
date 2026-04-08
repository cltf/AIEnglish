#!/usr/bin/env python3
"""Merge 阅读高频词汇 from transcribed tables; preserve 186–195 from previous JSON when missing."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "data" / "reading_high_freq.json"
OLD = OUT


def parse_tsv(s: str) -> dict[int, dict]:
    out = {}
    for line in s.strip().splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        parts = line.split("\t")
        if len(parts) < 5:
            continue
        rank = int(parts[0])
        out[rank] = {
            "rank": rank,
            "word": parts[1].strip(),
            "phonetic": parts[2].strip(),
            "meaning": parts[3].strip(),
            "frequency": int(parts[4]),
        }
    return out


# --- 1–30（标准 IPA，图 12）---
B01 = """
1	motivate	[ˈməʊtɪveɪt]	vt. 驱使，激发	42
2	plastic	[ˈplæstɪk]	n./adj. 塑料	41
3	positive	[ˈpɒzətɪv]	adj. 积极的，肯定的	38
4	physical	[ˈfɪzɪkl]	adj. 身体的，物理的	30
5	reduce	[rɪˈdjuːs]	v. 减少，缩小	30
6	performance	[pəˈfɔːməns]	n. 表现，表演	29
7	emotional	[ɪˈməʊʃənl]	adj. 情感的，情绪的	27
8	participant	[pɑːˈtɪsɪpənt]	n. 参加者	25
9	view	[vjuː]	n./vt. 风景，视野，见解；观看	24
10	cooperate	[kəʊˈɒpəreɪt]	vi. 合作，配合	22
11	negative	[ˈneɡətɪv]	adj. 负面的，消极的，肯定的	21
12	pressure	[ˈpreʃə(r)]	n. 压力	21
13	failure	[ˈfeɪljə(r)]	n. 失败	20
14	garbage	[ˈɡɑːbɪdʒ]	n. 垃圾	20
15	journal	[ˈdʒɜːnl]	n. 杂志，期刊，日志	20
16	device	[dɪˈvaɪs]	n. 设备	19
17	literature	[ˈlɪtrətʃə(r)]	n. 文学，文学作品	19
18	bacteria	[bækˈtɪəriə]	n. 细菌	18
19	landfill	[ˈlændfɪl]	n. 垃圾填埋场	18
20	personality	[ˌpɜːsəˈnæləti]	n. 性格，个性；特色，特征	18
21	solution	[səˈluːʃn]	n. 解决办法，答案	18
22	affect	[əˈfekt]	vt. 影响，感动，侵袭	17
23	average	[ˈævərɪdʒ]	n./adj. 平均数，平均水平；平均的	17
24	comfort	[ˈkʌmfət]	n./vt. 舒适，安慰	17
25	connection	[kəˈnekʃn]	n. 联系，连接	17
26	quality	[ˈkwɒləti]	n. 质量，品质，性质	17
27	violent	[ˈvaɪələnt]	adj. 猛烈的，暴力的	17
28	generate	[ˈdʒenəreɪt]	vt. 产生，引起	16
29	publish	[ˈpʌblɪʃ]	v. 出版	16
30	effective	[ɪˈfektɪv]	adj. 有效的	15
"""

# --- 31–61（图 3）---
B02 = """
31	gossip	[ˈɡɒsɪp]	n.流言蜚语	15
32	intelligence	[ɪnˈtelɪdʒəns]	n.智力，聪明，智能	15
33	reality	[riˈæləti]	n.真实，事实，现实	15
34	exploration	[ˌekspləˈreɪʃn]	n.探索，探究	14
35	species	[ˈspiːʃiːz]	n.种类，物种	14
36	cognitive	[ˈkɒɡnətɪv]	adj.认知的	13
37	compete	[kəmˈpiːt]	vi.竞争，比赛	13
38	disease	[dɪˈziːz]	n.疾病	13
39	evidence	[ˈevɪdəns]	n.证据	13
40	memorize	[ˈmeməraɪz]	v.记住	13
41	recycle	[ˌriːˈsaɪkl]	vt.回收	13
42	resource	[rɪˈsɔːs]	n.资源	13
43	argument	[ˈɑːɡjumənt]	n.争论(吵)，辩论，理由，论证	12
44	esteem	[ɪˈstiːm]	n./vt.尊重，敬重；把...看作，认为	12
45	injury	[ˈɪndʒəri]	n.伤害	12
46	powerful	[ˈpauəful]	adj.强大的，有权力的	12
47	throughout	[θruːˈaut]	prep./adv.遍及，贯穿；始终	12
48	appreciate	[əˈpriːʃieɪt]	vt.欣赏，感激，理解	11
49	gratitude	[ˈɡrætɪtjuːd]	n.感激	11
50	limited	[ˈlɪmɪtɪd]	adj.有限的	11
51	mental	[ˈmentl]	adj.精神的，心理的	11
52	outcome	[ˈautkʌm]	n.结果	11
53	reward	[rɪˈwɔːd]	n./vt.报酬，酬劳；奖赏	11
54	beneficial	[ˌbenɪˈfɪʃl]	adj.有益的，有利的	10
55	biological	[ˌbaɪəˈlɒdʒɪkl]	adj.生物学的	10
56	conclude	[kənˈkluːd]	v.得出结论，(使)结束	10
57	define	[dɪˈfaɪn]	vt.阐明，限定；给...下定义	10
58	package	[ˈpækɪdʒ]	n.包裹	10
59	psychological	[ˌsaɪkəˈlɒdʒɪkl]	adj.心理的；精神上的；心理学的	10
60	recall	[riˈkɔː(l)]	v.想起，召回	10
61	recipe	[ˈresəpi]	n.食谱，处方，秘诀	10
"""

# --- 62–92（图 1）---
B03 = """
62	replace	[rɪˈpleɪs]	vt.代替	10
63	respond	[rɪˈspɒnd]	v.反映，回应	10
64	status	[ˈsteɪtəs]	n.地位，身份，状态	10
65	strengthen	[ˈstreŋθən]	v.加强，巩固	10
66	stressed	[strest]	adj.焦虑不安的；心力交瘁的	10
67	unlock	[ˌʌnˈlɒk]	vt.开...的锁；揭示	10
68	willing	[ˈwɪlɪŋ]	adj.愿意的，乐意的	10
69	challenging	[ˈtʃælɪndʒɪŋ]	adj.有挑战性的	9
70	detail	[ˈdiːteɪl]	n.细节	9
71	digital	[ˈdɪdʒɪtəl]	adj.数字式的	9
72	discipline	[ˈdɪsɪplɪn]	n./v.学科，纪律；管教	9
73	exposure	[ɪkˈspəʊʒə]	n.暴露，显露，揭露	9
74	imagination	[ɪˌmædʒɪˈneɪʃən]	n.想象，想象力	9
75	literacy	[ˈlɪtərəsi]	n.识字；有文化	9
76	overweight	[ˌəʊvəˈweɪt]	n.超重	9
77	tend	[tend]	v.倾向，照料	9
78	track	[træk]	n./vt.踪迹，轨道，跑道，小径；跟踪	9
79	transportation	[ˌtrænspɔːˈteɪʃn]	n.运输	9
80	athlete	[ˈæθliːt]	n.运动员	8
81	climate	[ˈklaɪmət]	n.气候	8
82	desire	[dɪˈzaɪə(r)]	n./v.渴望，要求	8
83	distance	[ˈdɪstəns]	n.距离	8
84	exist	[ɪɡˈzɪst]	v.存在，生存	8
85	function	[ˈfʌŋkʃən]	n./vi 功能，机能，函数；运转，起作用	8
86	gradual	[ˈɡrædʒuəl]	adj.逐渐的	8
87	inspire	[ɪnˈspaɪə]	vt.鼓舞，给...以灵感	8
88	potential	[pəˈtenʃəl]	n./adj.潜能，可能性；可能的，潜在的	8
89	procrastinate	[prəʊˈkræstɪneɪt]	v.耽搁，拖延	8
90	puzzle	[ˈpʌzl]	n./v.谜；迷惑，使困惑	8
91	signal	[ˈsɪɡnəl]	n./v.信号；发信号，表明	8
92	structure	[ˈstrʌktʃə]	n.结构	8
"""

# --- 93–123（图 4）---
B04 = """
93	theory	[ˈθɪəri]	n. 理论	8
94	variety	[vəˈraɪəti]	n. 多样性, 种类	8
95	available	[əˈveɪləbl]	adj. 可用的, 可得到的, 有空的	7
96	boredom	[ˈbɔːdəm]	n. 无聊	7
97	charity	[ˈtʃærəti]	n. 慈善团体	7
98	combine	[kəmˈbaɪn]	vt. 联合, 结合	7
99	confused	[kənˈfjuːzd]	adj. 糊涂的, 迷惑的	7
100	escape	[ɪˈskeɪp]	vi. 逃脱, 逃走	7
101	exchange	[ɪksˈtʃeɪndʒ]	n./vt. 外汇, 兑换, 交换, 交流	7
102	expand	[ɪkˈspænd]	v. 扩大, 扩展	7
103	expression	[ɪkˈspreʃn]	n. 词语, 表示, 表达	7
104	increasingly	[ɪnˈkriːsɪŋli]	adv. 越来越多地, 不断增加地	7
105	loneliness	[ˈləʊnlinəs]	n. 孤独, 寂寞	7
106	movement	[ˈmuːvmənt]	n. 运动, 动作, 活动	7
107	observe	[əbˈzɜːv]	v. 观察, 遵守, 庆祝	7
108	option	[ˈɒpʃn]	n. 选择	7
109	particular	[pəˈtɪkjələ]	adj. 特别的, 讲究的, 挑剔的	7
110	reaction	[ri(ː)ˈækʃən]	n. 反应, 化学反应	7
111	charge	[tʃɑːdʒ]	vt. 收费, 管理, 充电, 冲刺, 控告	6
112	competence	[ˈkɒmpɪtəns]	n. 能力	6
113	concentrate	[ˈkɒnsəntreɪt]	v. 集中	6
114	consume	[kənˈsjuːm]	vt. 消耗, 消费	6
115	critical	[ˈkrɪtɪkl]	adj. 关键性的，批评的	6
116	decrease	[dɪˈkriːs]	v./n. 减少, 降低	6
117	ecology	[iˈkɒlədʒi]	n. 生态	6
118	enrich	[ɪnˈrɪtʃ]	n./vt. 使丰富	6
119	evaluate	[ɪˈvæljueɪt]	vt. 评价, 评估	6
120	exhibit	[ɪɡˈzɪbɪt]	n./vt. 展品, 展览; 表现, 显出	6
121	extracurricular	[ˌekstrəkəˈrɪkjələ]	adj. 课外的	6
122	independent	[ˌɪndɪˈpendənt]	adj. 独立的, 不相关的	6
123	interact	[ˌɪntərˈækt]	vi. 互动, 相互作用	6
"""

# --- 124–154（图 2）---
B05 = """
124	layer	[ˈleɪə(r)]	n./v. (分) 层次	6
125	major	[ˈmeɪdʒə]	n./adj./vi.主修，主修科目；主要的	6
126	pattern	[ˈpætən]	n.图案，样式，模式，榜样	6
127	popularity	[ˌpɒpjuˈlærəti]	n.受欢迎，普及	6
128	practical	[ˈpræktɪkəl]	adj.实用的，实际的	6
129	priority	[praɪˈɒrəti]	n.优先事项，优先(权)	6
130	privacy	[ˈpraɪvəsi]	n.隐私	6
131	productive	[prəˈdʌktɪv]	adj.多产的	6
132	routine	[ruːˈtiːn]	n.常规	6
133	sacrifice	[ˈsækrɪfaɪs]	n./vt.牺牲，祭祀，祭品	6
134	socialize	[ˈsəʊʃəlaɪz]	vt. (和他人) 交往，交际	6
135	storage	[ˈstɔːrɪdʒ]	n.贮藏，仓库	6
136	struggle	[ˈstrʌɡl]	n./v.挣扎	6
137	switch	[swɪtʃ]	n./v.开关，转变	6
138	trash	[træʃ]	n.垃圾	6
139	trick	[trɪk]	n./vt.诡计，把戏，诀窍；欺骗	6
140	trustworthy	[ˈtrʌstwɜːði]	adj.值得信任的，可靠的	6
141	virtual	[ˈvɜːtʃuəl]	adj.虚拟的	6
142	access	[ˈækses]	n./v.通道、路径，机会	5
143	adapt	[əˈdæpt]	vt.改编，适应	5
144	attack	[əˈtæk]	n./vt.攻击	5
145	carbon	[ˈkɑːbən]	n.碳	5
146	damage	[ˈdæmɪdʒ]	n./vt.损失，损害	5
147	display	[disˈpleɪ]	vt./n.陈列，展出	5
148	distract	[diˈstrækt]	vt.使分心	5
149	equipment	[ɪˈkwɪpmənt]	n.装备，器材	5
150	external	[ɪkˈstɜːnəl]	adj.外部的	5
151	feedback	[ˈfiːdbæk]	n.反馈	5
152	freedom	[ˈfriːdəm]	n.自由	5
153	indicator	[ˈɪndɪkeɪtə(r)]	n.指示信号，标志，迹象，指示器	5
154	intake	[ˈinteɪk]	n.吸收；吸入	5
"""

# --- 155–185（图 5）---
B06 = """
155	invitation	[ɪnvɪ'teɪʃən]	n. 邀请，招待	5
156	lifestyle	['laɪfstaɪl]	n. 生活方式	5
157	management	['mænɪdʒmənt]	n. 管理	5
158	metal	['metl]	n. 金属	5
159	mindful	['maɪndfl]	adj. 留心的，注意的	5
160	principle	['prɪnsəpl]	n. 道德原则，行为准则，法则	5
161	react	[ri'ækt]	vi. 反应	5
162	reasonable	['riːznəbl]	adj. 有道理的，合理的	5
163	release	[rɪ'liːs]	n./v. 释放，发行，发布	5
164	stare	[steə]	n./v. 凝视，注视	5
165	strategy	['strætədʒi]	n. 战略，策略	5
166	transport	['trænspɔːt]	n./vt. 运送，运输	5
167	trend	[trend]	n. 趋势，潮流	5
168	virus	['vaɪrəs]	n. 病毒	5
169	abuse	[ə'bjuːs]	vt. 滥用	4
170	adventure	[əd'ventʃə]	n. 冒险(活动)	4
171	ancestor	['ænsestə]	n. 祖先	4
172	artificial	[ˌɑːtɪ'fɪʃl]	adj. 人造的，假的	4
173	assess	[ə'ses]	v. 评估	4
174	assumption	[ə'sʌmpʃn]	n. 假定，承担	4
175	audience	['ɔːdiəns]	n. 听众，观众	4
176	balanced	['bælənst]	adj. 平衡的	4
177	bargain	['bɑːɡɪn]	n./vt. 交易，便宜货；讨价还价	4
178	battery	['bætəri]	n. 电池	4
179	bias	['baɪəs]	n./vt. 偏见，成见，偏好，偏爱	4
180	capsule	['kæpsjuːl]	n. 胶囊，密封舱	4
181	characteristic	[ˌkærəktə'rɪstɪk]	n./adj. 特性；特有的，典型的	4
182	comment	['kɒment]	n./vi. 评论	4
183	concept	['kɒnsept]	n. 概念	4
184	contain	[kən'teɪn]	vt. 包含，容纳	4
185	decade	['dekeɪd]	n. 十年，十年期	4
"""

# --- 218–248（图 6）---
B07 = """
218	treatment	[ˈtriːtmənt]	n.对待，治疗	4
219	volume	[ˈvɒljuːm]	n.卷，册，体积，容量，大量，音量	4
220	widespread	[ˈwaɪdspred]	adj.分布广泛的，普遍的	4
221	absolute	[ˈæbsəluːt]	adj.绝对的，完全的	3
222	admit	[ədˈmɪt]	v.承认，准许...进入，录取	3
223	aggression	[əˈɡreʃən]	n.进攻；侵略；侵犯	3
224	awesome	[ˈɔːsəm]	adj.可怕的，令人敬畏的，极好的	3
225	bake	[beɪk]	v./n.烘焙，烤	3
226	blame	[bleɪm]	n./vt.(坏事或错事的)责任；责备，归咎于	3
227	border	[ˈbɔːdə]	n./v.边界，边境；接壤	3
228	bother	[ˈbɒðə]	v.打扰，使烦恼，费心	3
229	civilization	[ˌsɪvəlaɪˈzeɪʃən]	n.文明	3
230	colleague	[ˈkɒliːɡ]	n.同事	3
231	complex	[ˈkɒmpleks]	adj.复杂的	3
232	concern	[kənˈsɜːn]	n./vt.关心；与...有关	3
233	conserve	[kənˈsɜːv]	vt.保存，节约	3
234	contribute	[kənˈtrɪbjuːt]	vt.捐献，贡献，是...的原因	3
235	credit	[ˈkredɪt]	n./v.信誉，学分，赞扬；归功于	3
236	crime	[kraɪm]	n.犯罪，罪行	3
237	criticism	[ˈkrɪtɪsɪzəm]	n.批评，评论	3
238	deadline	[ˈdedlaɪn]	n.截止日期	3
239	degrade	[dɪˈɡreɪd]	vt.贬低，使恶化，分解	3
240	delivery	[dɪˈlɪvəri]	n.传送；递送	3
241	demand	[dɪˈmɑːnd]	n./vt.(强烈)要求	3
242	dessert	[dɪˈzɜːt]	n.甜食，点心	3
243	discomfort	[disˈkʌmfət]	n./vt.不舒服；不适	3
244	discourage	[disˈkʌridʒ]	vt.使泄气，使沮丧，劝阻	3
245	economic	[iːkəˈnɒmɪk]	adj.经济的，经济学的	3
246	emergency	[iˈmɜːdʒənsi]	n.紧急情况	3
247	endanger	[inˈdeindʒə]	vt.危害，使受到危险	3
248	entertain	[ˌentəˈteɪn]	v.使...娱乐，招待	3
"""

# --- 249–279（图 9）---
B08 = """
249	exception	[ɪkˈsepʃən]	n. 例外，例外的人、事	3
250	extraordinary	[ɪkˈstrɔːdnri]	adj. 非凡的，不平常的	3
251	financial	[faɪˈnænʃl]	adj. 财政的，金融的	3
252	flexible	[ˈfleksəbl]	adj. 柔韧的，易弯曲的，灵活的	3
253	grateful	[ˈɡreɪtfl]	adj. 感谢的	3
254	greenhouse	[ˈɡriːnhaus]	n. 温室	3
255	handle	[ˈhændl]	n./v. 柄，把手；拿，处理，操纵，驾驭	3
256	healthcare	[ˈhelθkeə]	n. 健康护理	3
257	humanity	[hjuːˈmænəti]	n. 人类，人性；人文学科	3
258	import	[ˈɪmpɔːt]	n./v. 进口，输入	3
259	income	[ˈɪnkʌm]	n. 收入	3
260	inherit	[ɪnˈherɪt]	vt. 继承，经遗传获得	3
261	innovation	[ˌɪnəˈveɪʃn]	n. 创新	3
262	install	[ɪnˈstɔːl]	vt. 安装；设置	3
263	interpersonal	[ˌɪntəˈpɜːsənl]	adj. 人际的	3
264	lantern	[ˈlæntən]	n. 灯笼	3
265	length	[leŋθ]	n. 长度	3
266	location	[lə(u)ˈkeɪʃ(ə)n]	n. 位置	3
267	logical	[ˈlɒdʒɪkl]	adj. 符合逻辑的	3
268	male	[meɪl]	adj. 男性的；雄性的	3
269	multiple	[ˈmʌltɪpl]	n./adj. 倍数；多的	3
270	nationwide	[ˌneɪʃnˈwaɪd]	adj. 全国性的	3
271	native	[ˈneɪtɪv]	adj./n. 当地的；本地人	3
272	noticeable	[ˈnəʊtɪsəbl]	adj. 显著的；显而易见的	3
273	occasional	[əˈkeɪʒənl]	adj. 偶尔的	3
274	outgoing	[ˈaʊtɡəʊɪŋ]	adj. 开朗的	3
275	overlook	[ˌəʊvəˈlʊk]	v./n. 忽略；俯视	3
276	pack	[pæk]	n./v. 包裹；包装，打包	3
277	passive	[ˈpæsɪv]	adj. 消极的，被动的	3
278	poetry	[ˈpəʊɪtri]	n. 诗篇，诗歌	3
279	pollutant	[pəˈluːtənt]	n. 污染物	3
"""

# --- 280–310（图 7）---
B09 = """
280	preserve	[prɪ'zɜːv]	n./vt. 腌菜，泡菜，果酱；保存，保护	3
281	professional	[prə'feʃənəl]	adj./n. 专业的，职业的，专业人员	3
282	pure	[pjʊə(r)]	adj. 纯的，纯粹的，纯洁的	3
283	realistic	[rɪə'lɪstɪk]	adj. 现实的，实际的	3
284	relaxing	[rɪ'læksɪŋ]	adj. 轻松的，放松的	3
285	religious	[rɪ'lɪdʒəs]	adj. 宗教的，虔诚的	3
286	reserve	[rɪ'zɜːv]	n./vt. 保护区；保护，预定	3
287	reusable	[riː'juːzəbl]	adj. 可重复使用的	3
288	risky	['rɪski]	adj. 有风险的	3
289	roll	[rəʊl]	n./v. 卷形物，面包圈；滚动	3
290	satisfying	['sætɪsfaɪɪŋ]	adj. 令人满意（或满足）的	3
291	scan	[skæn]	n./v. 扫描，浏览	3
292	scared	[skeəd]	adj. 感到害怕的	3
293	scene	[siːn]	n. 景色，场景，现场	3
294	sculpture	['skʌlptʃə]	n. 雕刻	3
295	stable	['steɪbəl]	n./adj. 马厩；稳固的，稳定的，沉稳的	3
296	strength	[streŋθ]	n. 力气，毅力，优势	3
297	threat	['θret]	n. 威胁，征兆	3
298	thrill	[θrɪl]	n./v. 兴奋，激动，使非常兴奋	3
299	tiredness	['taɪə(r)dnɪs]	n. 疲劳	3
300	tiring	['taɪərɪŋ]	adj. 令人困倦的；使人疲劳的	3
301	update	[ˌʌp'deɪt]	v./n. 更新	3
302	version	['vɜːʃən]	n. 版本	3
303	wander	['wɒndə]	n./vi. 闲逛，漫游，漫步	3
304	weaken	['wiːkən]	v. (使)虚弱，削弱；动摇	3
305	whale	[weɪl]	n. 鲸鱼	3
306	willpower	['wɪlpaʊə(r)]	n. 意志力	3
307	wrap	[ræp]	vt. 包，裹	3
308	achievable	[ə'tʃiːvəbl]	adj. 可实现的	2
309	addiction	[ə'dɪkʃn]	n. 着迷，嗜好	2
310	admiration	[ˌædmə'reɪʃən]	n. 赞赏，钦佩	2
"""

# --- 311–341（图 11）---
B10 = """
311	advance	[əd'vɑ:ns][əd'væns]	n./v. 前进，进步，提前	2
312	affordable	[ə'fɔ:dəbl]	adj. 负担得起的	2
313	analysis	[ə'næləsis]	n. 分析	2
314	appeal	[ə'pi:l]	n./vi. 呼吁，上诉，吸引	2
315	application	[ˌæpli'keiʃ(ə)n]	n. 应用，申请	2
316	approach	[ə'prəutʃ]	n./v. 方法，途径；靠近	2
317	appropriate	[ə'prəuprieit]	adj. 恰当的	2
318	architecture	['ɑ:kitektʃə]	n. 建筑学(业)	2
319	artwork	['ɑ:twɜ:k]	n. 艺术作品	2
320	assignment	[ə'sainmənt]	n. 任务，作业	2
321	associated	[ə'səuʃieitid]	adj. 关联的	2
322	award	[ə'wɔ:d]	n. 奖，奖品	2
323	breakthrough	['breikθru:]	n. 突破	2
324	budget	['bʌdʒit]	n. 预算	2
325	calculation	[ˌkælkju'leiʃn]	n. 计算，估算	2
326	capable	['keipəbl]	adj. 有能力的	2
327	category	['kætigəri]	n. 种类，类别	2
328	channel	['tʃænəl]	n. 频道，渠道，途径，海峡	2
329	classic	['klæsik]	n./adj. 名著，经典著作；经典的，典型的	2
330	clinical	['klinikl]	adj. 临床的，临床诊断的	2
331	command	[kə'mɑ:nd]	n./v. 命令，指挥	2
332	companion	[kəm'pæniən]	n. 同伴，伴侣	2
333	comparison	[kəm'pærisən]	n. 比较	2
334	compromise	['kɔmprəmaiz]	n./v. 妥协，折衷	2
335	conference	['kɔnfərəns]	n. 会议	2
336	consequence	['kɔnsikwəns]	n. 后果	2
337	considerate	[kən'sidərit]	adj. 体贴的	2
338	constant	['kɔnstənt]	adj. 不变的，恒定的	2
339	cruel	['kru:əl]	adj. 残酷的，残忍的	2
340	curiosity	[ˌkjuəri'ɔsiti]	n. 好奇心	2
341	curriculum	[kə'rikjuləm]	n. 课程	2
"""

# --- 342–372（图 8）---
B11 = """
342	defense	[di'fens]	n.防御, 保卫	2
343	department	[di'pɑ:tmənt]	n.部, 部门, 系	2
344	dependable	[di'pendəbl]	adj.可信赖的; 可靠的	2
345	determination	[di,tɜ:mi'neiʃn]	n.决心, 决定	2
346	dilemma	[di'lemə]	n.(进退两难的)困境	2
347	dishonest	[dis'onist]	adj.不诚实的; 骗人的; 欺骗性的	2
348	dispose	[dis'pəuz]	v.安排, 布置, 去除, 处理	2
349	distribute	[di'stribju:t]	vt.分发, 分配	2
350	disturb	[di'stə:b]	vt.打扰, 妨碍	2
351	embrace	[im'breis]	n./vt.拥抱; 包括, 欣然接受, 乐意采纳	2
352	encounter	[in'kauntə(r)]	n./v.遭遇, 偶遇	2
353	encouragement	[in'kʌridʒmənt]	n.鼓励, 激励	2
354	enhance	[in'hɑ:ns]	vt.提高, 增加, 加强	2
355	ensure	[in'ʃuə]	vt.确保, 保证	2
356	evolution	[i:və'lu:ʃən]	n.进化	2
357	float	[fləut]	v.浮, 漂浮	2
358	fluid	['flu:id]	n./adj.液体	2
359	format	['fɔ:mæt]	n.格式	2
360	fortunate	['fɔ:tʃənət]	adj.幸运的	2
361	foundation	[faun'deiʃən]	n.地基, 基础	2
362	fragile	['frædʒail]	adj.易损坏的	2
363	frightened	['fraitnd]	adj.害怕的, 受惊吓的	2
364	furniture	['fɜ:nitʃə(r)]	n.家具	2
365	generous	['dʒenərəs]	adj.慷慨的, 丰富的	2
366	gesture	['dʒestʃə]	n./v.姿势, 手势, 姿态; 作手势	2
367	graduate	['ɡrædʒueit]	n./vi.毕业生; 毕业	2
368	grain	[ɡrein]	n.谷物, 谷粒	2
369	grand	[ɡrænd]	adj.宏伟的, 壮丽的	2
370	guidance	['ɡaidəns]	n.指导, 领导	2
371	identify	[ai'dentifai]	vt.认出, 识别	2
372	ignore	[iɡ'nɔ:(r)]	vt.不理会, 忽视	2
"""

# --- 405–435（图 10）---
B12 = """
405	plate	[pleit]	n.盘子，碟，薄板	2
406	poisonous	[ˈpɔizənəs]	vt./adj.使中毒，毒害；有毒的	2
407	precious	[ˈpreʃəs]	adj.珍贵的	2
408	promote	[prəuˈməut]	vt.促进，晋升，促销	2
409	rebuild	[ˌri:ˈbild]	vt.重建	2
410	recreate	[ˌri:kriˈeit]	vt.再现；再创造	2
411	reflect	[riˈflekt]	vt.反射，反映，反思	2
412	relation	[riˈleiʃn]	n.关系	2
413	relevant	[ˈreləvənt]	adj.相关的	2
414	relief	[riˈli:f]	n.减轻，宽慰，轻松，解脱	2
415	resistant	[riˈzistənt]	adj.有抵抗力的，抵抗的；顽固的	2
416	route	[ru:t]	n.路线	2
417	sample	[ˈsɑ:mp(ə)l]	n.样本，例子	2
418	selfish	[ˈselfiʃ]	adj.自私的	2
419	slip	[slip]	n./vi.滑倒，滑落	2
420	snack	[snæk]	n./vi.小吃；点心	2
421	solar	[ˈsəulə(r)]	adj.太阳的，太阳能的	2
422	spiritual	[ˈspiritʃuəl]	adj.精神上的，心灵的	2
423	stimulate	[ˈstimjuleit]	vt.刺激，激发	2
424	summarize	[ˈsʌməraiz]	vt.总结	2
425	talented	[ˈtæləntid]	adj.有才能的，有才干的	2
426	tease	[ti:z]	v.取笑，逗弄	2
427	tension	[ˈtenʃ(ə)n]	n. (情况、心情) 紧张	2
428	thoughtful	[ˈθɔ:tfəl]	adj.深思熟虑的，体贴的	2
429	tongue	[tʌŋ]	n.舌头，语言	2
430	tough	[tʌf]	adj.艰难的，严厉的，坚强的	2
431	trial	[ˈtraiəl]	n.审判，试用	2
432	tricky	[ˈtriki]	adj.难办的；难对付的，狡猾的；诡计多端的	2
433	trigger	[ˈtriɡə]	n/v.扳机，起因；引起	2
434	undertake	[ˌʌndəˈteik]	vt.承担，着手做，同意	2
435	universe	[ˈju:nivə:s]	n.宇宙，全世界	2
"""


def main() -> None:
    merged: dict[int, dict] = {}
    for block in (
        B01,
        B02,
        B03,
        B04,
        B05,
        B06,
        B07,
        B08,
        B09,
        B10,
        B11,
        B12,
    ):
        merged.update(parse_tsv(block))

    # 186–195：图源未给连续表，沿用上一版 JSON
    if OLD.exists():
        with OLD.open(encoding="utf-8") as f:
            old = json.load(f)
        for e in old.get("entries", []):
            r = e["rank"]
            if 186 <= r <= 195:
                merged[r] = e

    entries = [merged[r] for r in sorted(merged)]
    doc = {
        "version": 2,
        "label": "阅读高频词汇",
        "note": "",
        "entries": entries,
    }
    OUT.write_text(json.dumps(doc, ensure_ascii=False, indent=2), encoding="utf-8")
    print("written", OUT, "entries", len(entries), "ranks", entries[0]["rank"], "..", entries[-1]["rank"])


if __name__ == "__main__":
    main()
