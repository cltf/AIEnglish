#!/usr/bin/env python3
"""Emit web/data/essays.json from embedded source (single source of truth)."""
import json
from pathlib import Path

from chinese_exams_data import CHINESE_EXAMS_EXTRA

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "data" / "essays.json"

EXAMS = [
    {
        "id": "beijing-2024",
        "title": "2024年北京中考英语作文",
        "topics": """题目（二选一）
题目①：交流活动设计假设你是李华，你校英语社团将接待国外学生代表团来访，届时举办一次交流活动，为此在校内征集师生的建议。请你给英语社团公众号留言，提供一个交流活动设计并说明理由。
提示词语：visit, performance, make, opportunity, culture内容提示：What do you suggest for the activity? Why do you suggest doing that?
题目②：记好友，叙友情某英文网站正在开展以"记好友，叙友情"为主题的征文活动。假设你是李华，请你用英文写一篇短文投稿，介绍你最好的一位朋友，并记述你们之间一段难忘的经历。""",
        "samples": [
            {
                "id": "beijing-2024-s1",
                "title": "范文1 - 交流活动设计（传统文化表演）",
                "body": """I'm Li Hua from Class 1, Grade 9. I suggest organizing a traditional Chinese culture show for the visiting students. 
First, we can prepare some traditional performances like Peking Opera, Chinese folk dances, and calligraphy shows. This will give them a great opportunity to experience Chinese culture. Second, we can invite them to try some traditional crafts like paper-cutting and making dumplings. It will make their visit more interesting and memorable.
I believe this activity can help build a bridge between us and the foreign students, and promote cultural exchange. I hope my suggestion will be helpful.""",
            },
            {
                "id": "beijing-2024-s2",
                "title": "范文2 - 交流活动设计（校园参观+互动）",
                "body": """I'm Li Hua from Class 1, Grade 9. I suggest organizing a campus tour with interactive activities for the foreign students.
First, we can show them around our school, including the library, science labs, and art rooms. Then, we can arrange a friendly basketball match between us and them. Sports can break down barriers and help us make friends quickly. Finally, we can have a dinner party where students from both sides can share their school life and hobbies.
This activity will create a relaxed atmosphere and give everyone a chance to communicate freely.""",
            },
            {
                "id": "beijing-2024-s3",
                "title": "范文3 - 记好友，叙友情",
                "body": """My best friend is Wang Wei. We have been classmates since Grade 7. He is tall and handsome, with a warm smile that always makes people feel welcome.
Last semester, I was seriously ill and had to stay in hospital for a week. Wang Wei came to visit me every day after school. He brought me notes and explained what the teachers taught in class. He even helped me with my math problems at my bedside. I was deeply moved by his kindness.
True friendship is not just about having fun together, but about being there when you need each other. I feel lucky to have Wang Wei as my best friend.""",
            },
            {
                "id": "beijing-2024-s4",
                "title": "范文4 - 记好友，叙友情",
                "body": """My best friend is Li Mei. She has long black hair and bright eyes. She is kind, helpful, and always ready to help others.
One rainy day, I forgot to bring my umbrella. After school, I stood at the school gate, wondering how to get home. Li Mei saw me and offered to share her umbrella with me. We walked home together under the small umbrella. Halfway home, I found that her left shoulder was completely wet, but she was still smiling and chatting with me happily.
That moment made me realize what true friendship means. I'm grateful to have such a good friend.""",
            },
            {
                "id": "beijing-2024-s5",
                "title": "范文5 - 交流活动设计（英语角+文化展示）",
                "body": """I'm Li Hua from Class 1, Grade 9. I recommend organizing an English Corner with cultural exhibitions for the visiting students.
We can set up different exhibition areas about Chinese festivals, traditional costumes, and famous historical places. Each area can have student volunteers to introduce the culture in English. After the tour, we can have a tea party where students from both countries can talk freely about their interests and school life.
This activity will not only improve our English speaking skills but also help us make international friends and understand different cultures better.""",
            },
            {
                "id": "beijing-2024-s6",
                "title": "范文6 - 剪纸工作坊（译文+要点）",
                "body": """【英文范文】
I'm Li Hua from Class 1, Grade 9. For the foreign students visiting our school, I'd suggest a workshop on paper cutting. A teacher can first introduce its history and artistic value. Then we can help them to make paper cuttings. This activity will give them an opportunity to have a try at a traditional Chinese art form. What's more, when they take these paper cuttings back to their home country and give them to their families as gifts, more people there may enjoy the beauty of Chinese culture.
I hope my idea can be considered. Thanks.

【中文翻译】
我是九年级一班的李华。针对来访我校的外国学生，我提议举办一场剪纸工作坊。老师可以先介绍剪纸的历史和艺术价值，之后我们帮助他们制作剪纸。这项活动能让他们有机会尝试中国传统艺术形式。此外，当他们把剪纸带回自己的国家并作为礼物送给家人时，更多人或许能领略到中国文化的美。
我希望我的想法能被采纳。谢谢。

【写作要点】
开篇介绍身份，明确提议的活动（剪纸工作坊）；
说明活动的具体流程（讲解知识→动手制作）；
阐述活动的意义（体验传统艺术、传播中国文化）；
结尾表达希望想法被采纳的意愿。""",
            },
            {
                "id": "beijing-2024-s7",
                "title": "范文7 - 记好友，叙友情（译文+要点）",
                "body": """【英文范文】
I'm glad to say something about my best friend. Her name is Alice. She is helpful and always has a sweet smile on her face. She is really good at dancing. Last term, there was a dancing festival in our school. We both signed up for it, but I was a little shy at first and afraid of dancing in front of a large crowd of people. She encouraged me a lot and helped me practice the moves. Together, we gave a very successful performance. I will never forget this experience.

【中文翻译】
我很高兴能说说我最好的朋友。她叫爱丽丝，为人乐于助人，脸上总是带着甜美的笑容，舞跳得非常好。上学期学校举办了舞蹈节，我们俩都报名参加了。起初我有点害羞，害怕在一大群人面前跳舞，她给了我很多鼓励，还帮我练习动作。我们一起完成了一场非常成功的表演，我永远不会忘记这段经历。

【写作要点】
先点明写作对象（最好的朋友），介绍朋友的性格和特长；
用具体事件（舞蹈节合作）体现朋友的品质；
结尾表达对这段经历的难忘之情。""",
            },
        ],
    },
    {
        "id": "beijing-2025",
        "title": "2025年北京中考英语作文",
        "topics": """题目（二选一）
题目①：做家务调查假设你是李华，你们班的英语课正在开展研究性学习，你选择研究同学们做家务的情况，为此你对班里40名同学进行了问卷调查。
提示词语：show, chores, spend, tidy up, helpful内容提示：What can you learn from the results?
题目②：Dream Library 梦想图书馆假设你是李华，你校正在开展"书香校园"活动，英语社团公众号打算做一期以"Dream Library"为主题的推送，为此在校内征集想法。
提示词语：recommend, new, experience, explore, interest内容提示：What is your dream library like? How can students benefit from your idea?""",
        "samples": [
            {
                "id": "beijing-2025-s1",
                "title": "范文1 - 做家务调查",
                "body": """I'm Li Hua from Class 1, Grade 9. I did a survey about my classmates' housework habits. The results show that 25% of students often do housework, 50% do it sometimes, and 25% hardly ever do it. Among the chores, washing dishes is the most common at 30%, followed by sweeping and mopping at 25%.
From the results, we can see that most students have some awareness of doing housework, but only a quarter do it regularly. Doing housework is not only a way to share the burden of family, but also a good way to exercise ourselves.
I suggest all classmates spend at least 30 minutes daily on housework. Start with simple tasks like tidying your own room. Remember, being helpful at home can make our family life happier and help us grow into responsible people.""",
            },
            {
                "id": "beijing-2025-s2",
                "title": "范文2 - Dream Library",
                "body": """I'm Li Hua from Class 1, Grade 9. I recommend creating a "Dream Library" with different themed reading zones.
My dream library would have a quiet study area with comfortable sofas and natural lighting. There would also be a digital reading zone with tablets and e-books for students to explore new technologies. Additionally, I suggest having a creative corner where students can share book reviews and recommendations.
This library would benefit students in many ways. It can create a new reading experience and spark our interest in learning. Students can explore various topics and broaden their horizons. I hope my dream will come true. Thanks.""",
            },
            {
                "id": "beijing-2025-s3",
                "title": "范文3 - 做家务调查",
                "body": """I'm Li Hua from Class 1, Grade 9. According to my survey on 40 classmates, I found that half of them do housework occasionally, while only a quarter do it regularly. The most popular chore is washing dishes, and tidying rooms ranks third.
These results show that many students still need to improve their participation in housework. Sharing housework is important because it teaches us responsibility and independence. It also helps our parents reduce their daily burden.
I suggest we should form good habits by doing housework regularly. Even small efforts like making our beds or cleaning our desks can make a big difference. Let's take action and be helpful family members.""",
            },
            {
                "id": "beijing-2025-s4",
                "title": "范文4 - Dream Library",
                "body": """I'm Li Hua from Class 1, Grade 9. I want to recommend a "Dream Library" design for our school.
In my mind, the dream library should have three main areas: a silent reading room with soft music, a group discussion area with round tables, and a multimedia zone where we can watch educational videos. I also suggest adding some green plants to make the space more comfortable.
This design can bring many benefits. It offers students a new way to experience reading and learning. The different zones allow us to choose how we want to study. It will surely spark our interest in books and help us explore knowledge more effectively. I hope my idea can be realized.""",
            },
            {
                "id": "beijing-2025-s5",
                "title": "范文5 - Dream Library",
                "body": """I'm Li Hua from Class 1, Grade 9. For the "Dream Library" theme, I recommend building a modern and cozy library space.
My dream library would feature floor-to-ceiling bookshelves, comfortable reading nooks by the windows, and a small café area where students can enjoy coffee while reading. There should also be a 24-hour self-service borrowing system for convenience.
Students can benefit greatly from this library. It provides a peaceful environment for focused study and encourages us to develop good reading habits. The comfortable setting can make reading a more enjoyable experience and help us discover new interests. I hope my dream will come true. Thanks!""",
            },
            {
                "id": "beijing-2025-s6",
                "title": "范文6 - 做家务调查（译文+要点）",
                "body": """【英文范文】
I did a survey on my classmates doing housework. The results show that 32 students sometimes do housework. Six students do it regularly, and two seldom carry out their household chores. This shows that most students don't do housework very often and may not spend much time on it.
In my opinion, we should try to help out more. Small things like washing the dishes and tidying up can make a difference. It would be very helpful if we can help our parents prepare meals or clean the house on weekends.

【中文翻译】
我对同学们做家务的情况做了一项调查。结果显示，32 名学生偶尔做家务，6 名学生经常做，还有两名很少做家事。这表明大多数学生不常做家务，可能在这上面花的时间也不多。
在我看来，我们应该多帮忙。洗碗、整理房间这类小事也能起到作用。如果我们能在周末帮父母做饭或打扫房子，会非常有帮助。

【写作要点】
先说明调查对象和结果，用具体数字体现做家务的不同频率；
表达自己的观点，强调做家务的意义；
列举具体的家务行为，给出可行的建议。""",
            },
            {
                "id": "beijing-2025-s7",
                "title": "范文7 - Dream Library（译文+要点）",
                "body": """【英文范文】
I'm Li Hua from Class 1, Grade 9. My dream library could recommend books to students according to their borrowing records. It could also offer new ways to create special reading experiences. For example, students could use VR technology to explore historical events or scientific discoveries described in the books they are reading. My dream library could help students to be more closely connected with books and develop a stronger interest in reading.
I hope my dream will come true. Thanks.

【中文翻译】
我是九年级一班的李华。我梦想中的图书馆能根据学生的借阅记录为他们推荐书籍，还能提供新方式打造特别的阅读体验。比如，学生可以用虚拟现实技术探索他们正在阅读的书中描述的历史事件或科学发现。我梦想的图书馆能帮助学生与书籍建立更紧密的联系，培养更浓厚的阅读兴趣。
我希望我的梦想能实现。谢谢。

【写作要点】
介绍自身身份，点明梦想图书馆的核心功能（智能荐书）；
举例说明创新阅读体验的方式（VR 技术的应用）；
说明图书馆的意义，最后表达对梦想实现的期待。""",
            },
        ],
    },
    {
        "id": "beijing-2023",
        "title": "2023年北京中考英语作文",
        "topics": """题目（二选一）
题目①：邀请信（植树活动）假设你是李华，请给你的英国朋友 Peter 写一封邮件，邀请他参加毕业前夕同学们一起为校园植树的活动，并说明时间、地点与活动安排。
提示词语：invite, plant, gate, dig, hope
题目②：社团经历同学们参加过各种各样的社团，如篮球队、合唱团等。假设你是李华，请你用英语写一篇短文给学校英文网站投稿，介绍一个你参加过的社团，谈谈这个社团的活动内容以及你的收获。
提示词语：take part in, practice, skill, benefit, progress提示问题：What club did you join? What did you do in the club? What have you learned from the experience?""",
        "samples": [
            {
                "id": "beijing-2023-s1",
                "title": "范文1 - 英语戏剧社团",
                "body": """I joined the English Drama Club last year. It has become one of the most meaningful experiences in my middle school life.
In the club, we meet every Tuesday afternoon. We practice English pronunciation and acting skills by performing short plays. Last semester, we put on a performance of Romeo and Juliet for the school art festival. I played the role of Juliet. At first, I was shy to speak English in front of others, but the warm atmosphere encouraged me to keep trying.
Through this experience, I've improved my spoken English significantly. I also learned the importance of teamwork and built my confidence. As the saying goes, "Practice makes perfect." I am grateful for this valuable experience.""",
            },
            {
                "id": "beijing-2023-s2",
                "title": "范文2 - 篮球社团",
                "body": """Among all the clubs in our school, I chose to join the Basketball Club, and it has brought me many benefits.
We practice three times a week after school. Our coach teaches us basic skills like dribbling, passing, and shooting. Besides regular training, we also take part in friendly matches with other schools. Last month, we won the second place in the city middle school basketball tournament. I was the point guard of our team.
Through this club, I've not only improved my basketball skills but also learned about teamwork and perseverance. Playing basketball has made me stronger physically and mentally. I believe this experience will benefit my future growth.""",
            },
            {
                "id": "beijing-2023-s3",
                "title": "范文3 - 科学社团",
                "body": """I am a member of the Science Club, which has sparked my curiosity and passion for exploring the world.
In the club, we do various experiments and scientific projects. For example, we once built a small robot using recycled materials. When I saw it move for the first time, I felt extremely proud. We also visit science museums and invite scientists to give us lectures about their research.
This club has taught me to think critically and solve problems creatively. I've learned that science is not just about textbooks but about observing and questioning the world around us. This experience has inspired me to pursue a career in science in the future.""",
            },
            {
                "id": "beijing-2023-s4",
                "title": "范文4 - 合唱社团",
                "body": """Joining the School Choir was one of the best decisions I've made in middle school.
We practice singing every Wednesday and Friday afternoon. Our teacher, Miss Wang, teaches us how to breathe properly and sing in harmony. Last semester, we performed at the city's New Year concert. Standing on the stage with my teammates, singing beautiful songs together, was an unforgettable moment.
Through this experience, I've learned the importance of cooperation and discipline. Everyone must listen to each other and work together to create beautiful music. I've also made many friends who share the same interest. Music has become an important part of my life.""",
            },
            {
                "id": "beijing-2023-s5",
                "title": "范文5 - 环保志愿者社团",
                "body": """I joined the Environmental Protection Club two years ago, and it has changed my view of the world.
We organize various activities to protect the environment. We plant trees in spring, clean up the park on weekends, and promote recycling in our school. Last year, we started a "No Plastic Week" campaign, encouraging students to use reusable bags and bottles. Many classmates joined us, and we collected over 500 signatures for our proposal.
Through these activities, I've realized that even small actions can make a big difference. I've become more responsible and aware of environmental issues. This experience has taught me that everyone can contribute to making our planet better.""",
            },
            {
                "id": "beijing-2023-s6",
                "title": "范文6 - 邀请信·给 Peter 的植树邀请（译文+要点）",
                "body": """【英文范文】
Dear Peter,
How is everything going? I'm writing to invite you to join us. As we are going to graduate, our classmates plan to plant a tree together.
We all know that trees can stop the wind from blowing the sand, and they can also make our school beautiful. So we decide to plant a tree for our school. Next Sunday, we will meet at the gate of the school and then go to the playground. We will dig a hole, plant the tree and then water it. I believe we all will have a great time.
Hope to hear from you soon.
Yours,
Li Hua

【中文翻译】
亲爱的彼得：
你好吗？我写信是想邀请你加入我们。因为我们即将毕业，同学们计划一起种一棵树。
我们都知道，树木可以防风固沙，还能让我们的校园更美丽。所以我们决定为学校种一棵树。下周日，我们会在学校门口集合，然后去操场。我们会挖坑、种树，再给树浇水。相信我们都会度过一段愉快的时光。
期待你的回信。
此致
李华

【写作要点】
格式规范：标准英文书信格式（称呼、正文、落款），符合中考写作要求；
逻辑清晰：开篇问候并说明写信目的（邀请）；活动背景（毕业留念）与植树的意义（环保、美化校园）；详细说明活动时间、地点与流程；结尾表达期待并礼貌收尾；
高分亮点：invite sb to do sth、stop...from doing sth、hope to hear from you；用一般将来时描述活动安排，符合语境。""",
            },
            {
                "id": "beijing-2023-s7",
                "title": "范文7 - 篮球俱乐部经历（译文+要点）",
                "body": """【英文范文】
I have been in the basketball club for three years. We used to practice from Monday to Friday, and on the weekend, we played games with other teams. During the three years, my skill of playing basketball improved a lot. And I also made a lot of friends. Playing basketball not only helps me keep fit, but also helps me understand the importance of teamwork. I will keep playing it and try to be good at it.

【中文翻译】
我加入篮球俱乐部已经三年了。我们过去从周一到周五训练，周末和其他队伍打比赛。这三年里，我的篮球技术进步了很多，也交到了很多朋友。打篮球不仅帮我保持健康，还让我明白了团队合作的重要性。我会一直坚持打篮球，努力把它练好。

【写作要点】
结构完整：开篇点明经历（加入篮球俱乐部三年）；描述日常训练与比赛；说明收获（技术提升、交友、健康、团队精神）；结尾表达坚持；
高分亮点：现在完成时（have been in）与一般过去时（used to practice）、一般现在时（helps）搭配准确；not only...but also... 并列结构；主题积极，符合中考评分标准。""",
            },
        ],
    },
    {
        "id": "beijing-2022",
        "title": "2022年北京中考英语作文",
        "topics": """题目（二选一）
题目①：中国社交礼仪假定你是李华，你的英国笔友Chris对中国文化很感兴趣，他给你发来邮件，询问中国人日常生活中的基本社交礼仪，如待人接物、餐桌礼仪等。请你用英语回复一封邮件，介绍你所知道的相关礼仪。
提示词语：manners, shake hands, polite, gift, chopsticks提示问题：What do we do when we meet people for the first time? How do we behave properly at table?
题目②：提升效率某英文网站正在开展以"提升效率"为主题的征文活动。假定你是李华，请用英语写一篇短文投稿，谈谈你在学习或生活中是如何提升效率的，取得了什么效果。
提示词语：plan, task, attention, make use of, progress提示问题：How do you increase your efficiency? What have you achieved?""",
        "samples": [
            {
                "id": "beijing-2022-s1",
                "title": "范文1 - 中国社交礼仪",
                "body": """Dear Chris,
I'm glad to know that you're interested in Chinese social manners. Let me share some with you.
When we meet people for the first time, we usually say hello with a smile. Sometimes we shake hands, especially in formal situations. It's polite to address elderly people with respectful titles. If you visit someone's home, bringing a small gift like fruit or tea is considered thoughtful.
At table, there are some important manners to remember. We use chopsticks instead of knives and forks. It's impolite to stick chopsticks upright in the rice bowl. Also, we usually wait for elders to start eating first. Don't tap the bowl with chopsticks, as it's considered rude.
I hope these tips will help you when you visit China. Looking forward to seeing you!
Yours,Li Hua""",
            },
            {
                "id": "beijing-2022-s2",
                "title": "范文2 - 提升效率",
                "body": """High efficiency is important for a busy student. I'd like to share how I increase my efficiency in daily life.
First, I always make a good plan at the beginning of each day. I list all my tasks and prioritize them according to their importance. Second, I focus my full attention on one task at a time instead of multitasking. When studying, I put my phone away to avoid distractions. Third, I make good use of碎片时间, such as reviewing vocabulary while waiting for the bus.
Through these methods, I have made great progress. I can finish my homework more quickly and have more time for hobbies and exercise. My grades have also improved significantly. Good planning really makes a difference!""",
            },
            {
                "id": "beijing-2022-s3",
                "title": "范文3 - 中国社交礼仪",
                "body": """Dear Chris,
I'm excited to tell you about Chinese social etiquette. These customs have been passed down for thousands of years.
In daily interactions, being polite and respectful is essential. When meeting someone for the first time, a gentle handshake and a slight nod are appropriate. We often exchange business cards with both hands to show respect. When speaking, maintaining eye contact shows sincerity.
Dining etiquette is particularly important in Chinese culture. We share dishes from communal plates placed in the center of the table. It's polite to serve elders first and offer them the best pieces. Chopsticks should never be pointed at others or left standing in rice, as these actions are associated with funerals.
I hope you'll enjoy experiencing these customs in person when you visit China!
Yours,Li Hua""",
            },
            {
                "id": "beijing-2022-s4",
                "title": "范文4 - 提升效率",
                "body": """As a middle school student, I used to struggle with managing my time. Now I've found some effective ways to improve my efficiency.
My first strategy is to create a detailed schedule every morning. I break large tasks into smaller, manageable steps. For example, instead of just writing "study math," I specify "finish 10 algebra problems." Second, I use the Pomodoro Technique - studying for 25 minutes, then taking a 5-minute break. This keeps my mind fresh and focused.
These methods have brought me remarkable results. I used to spend three hours on homework, but now I can finish in two hours. I've also found time to read books and learn the guitar. Efficiency is not about working harder, but working smarter.""",
            },
            {
                "id": "beijing-2022-s5",
                "title": "范文5 - 中国社交礼仪",
                "body": """Dear Chris,
It's wonderful to hear about your interest in Chinese culture. Let me introduce some basic social etiquette to you.
When greeting people, we typically offer a friendly smile and say "Ni hao." Among friends, a light handshake is common, but hugs are becoming more popular among young people. When visiting someone's home, it's customary to remove your shoes at the entrance. Bringing a gift is appreciated but not mandatory.
At meals, several customs are worth noting. We usually wait for the host to invite us to start eating. It's polite to try a bit of everything offered to show appreciation. When using chopsticks, never point them at others or play with them. Also, burping at the table is actually considered a compliment to the chef in some regions!
Don't worry too much though - Chinese people are generally understanding toward foreigners learning our customs.
Yours,Li Hua""",
            },
        ],
    },
    {
        "id": "beijing-2021",
        "title": "2021年北京中考英语作文",
        "topics": """题目（二选一）
题目①：北京文化标志 (Cultural Symbols of Beijing)
北京是一座蕴含着丰富文化的城市，这里的建筑、戏曲、美食、服饰、手工艺品等，都有文化的印记。假如你是李华，你校英语社团正在开展线上国际交流活动，其公众号计划做关于北京文化的系列推送，现就"Cultural Symbols of Beijing(北京文化标志)"这一话题在校内收集素材。请你用英语给公众号留言，推荐一个你眼中的北京文化标志，对其作简要介绍，并说明推荐理由。

提示词语：show, traditional, love, treasure, important
提示问题：In your opinion, what's the cultural symbol of Beijing? Please describe it. Why do you think it can be a symbol?

题目②：适应变化
现实中，大到自然环境和社会环境，小到个人生活和学习，变化无处不在。面对变化，唯有积极适应，才能有所进步和收获。某英文网站正在开展以"适应变化"为主题的征文活动，假如你是李华，请用英语写一篇短文投稿，谈谈你生活中的一个变化，你是如何应对这个变化的，以及有什么收获。

提示词语：help, enjoy, new, challenge, take action
提示问题：What is the change that happened in your life? How did you deal with it? What have you learned from it?

以下补充范文对应题目：
题目①（书信）：假定你是李华，你的英国笔友 Chris 对中国文化很感兴趣，来信询问中国人日常生活中的基本社交礼仪。请你用英语回复一封邮件，介绍相关礼仪。
提示词语：manners, shake hands, polite, gift, chopsticks
题目②（议论文）：某英文网站正在开展以「提高学习效率」为主题的征文活动，假定你是李华，请用英语写一篇短文投稿，谈谈你在学习或生活中如何提升效率及收获。
提示词语：plan, task, attention, make use of, progress""",
        "samples": [
            {
                "id": "beijing-2021-s1",
                "title": "范文1 - 北京文化标志（故宫）",
                "body": """I'm Li Hua from Class 1, Grade 9. I think the Forbidden City is an important cultural symbol of Beijing.

The Forbidden City, also known as the Palace Museum, is located in the center of Beijing. It was built during the Ming Dynasty and has a history of over 600 years. With its magnificent buildings and traditional design, it shows the wisdom and creativity of ancient Chinese people. The red walls and golden roofs are really impressive.

I recommend it because it represents traditional Chinese culture. Visiting the Forbidden City helps us understand our history better and treasure our cultural heritage. It's a place where we can feel proud of our nation and love our motherland more deeply.""",
            },
            {
                "id": "beijing-2021-s2",
                "title": "范文2 - 适应变化",
                "body": """Changes are everywhere in life. The biggest change in my life happened last year when I entered middle school.

At first, I felt nervous because everything was new - new teachers, new classmates, and more challenging subjects. I had difficulty managing my time and often felt stressed. However, I decided to take action instead of complaining. I made a study plan, asked teachers for help when needed, and tried to make new friends by joining the English club.

Now I enjoy my school life very much. This experience taught me that facing challenges bravely can help us grow. I've learned that adapting to change is an important life skill.""",
            },
            {
                "id": "beijing-2021-s3",
                "title": "范文3 - 北京文化标志（京剧）",
                "body": """I'm Li Hua from Class 1, Grade 9. In my opinion, Peking Opera is a wonderful cultural symbol of Beijing.

Peking Opera is a traditional form of Chinese theater with a history of more than 200 years. It combines music, vocal performance, mime, dance, and acrobatics. The performers wear beautiful costumes and paint their faces with special patterns to show different characters. The stories usually come from Chinese history and legends.

I believe Peking Opera is important because it represents the essence of Chinese traditional art. It's not just entertainment but also a treasure of our cultural heritage. We should help spread this art form to the world and let more people love it.""",
            },
            {
                "id": "beijing-2021-s4",
                "title": "范文4 - 适应变化",
                "body": """Last semester, my family moved to a new neighborhood, which was a big change for me.

I had to leave my old friends and change to a new school. At first, I felt lonely and missed my old life very much. But then I realized I needed to adapt to the new environment. I started to explore my new community, joined the school's basketball team, and tried to talk to my new classmates actively.

Gradually, I made many new friends and found the new school was actually great. This change helped me become more independent and outgoing. I've learned that change can bring new opportunities if we face it with a positive attitude.""",
            },
            {
                "id": "beijing-2021-s5",
                "title": "范文5 - 北京文化标志（北京烤鸭）",
                "body": """I'm Li Hua from Class 1, Grade 9. I recommend Beijing Roast Duck as a cultural symbol of Beijing.

Beijing Roast Duck is a world-famous dish with a history of hundreds of years. The ducks are roasted in a special oven until the skin becomes crispy and golden. The traditional way to eat it is to wrap slices of duck with cucumber and special sauce in thin pancakes. It tastes delicious!

I think it represents Beijing culture because it's not just food but also an art form. Making authentic Beijing Roast Duck requires special skills and experience. When foreign friends visit Beijing, trying this dish is a must. It shows the rich food culture of our city and helps people understand Chinese hospitality.""",
            },
            {
                "id": "beijing-2021-s6",
                "title": "范文6 - 中国社交礼仪书信（译文+要点）",
                "body": """【英文范文】
Dear Chris,
I'm glad to know that you're interested in Chinese social manners. China is a country with a long history, so there are many customs here. Let me tell you some of them.
Firstly, you are supposed to shake hands when you meet someone for the first time. Secondly, you should bring a gift when you are invited to a party and be sure to arrive on time or a few minutes earlier. Thirdly, when you eat meals with others, it's rude to point at others with your chopsticks. And don't make noise when you have your soup.
I hope the above is helpful. Please feel free to ask for more information.
Yours,
Li Hua

【中文翻译】
亲爱的克里斯：
很高兴得知你对中国的社交礼仪感兴趣。中国是一个历史悠久的国家，因此有许多习俗。让我为你介绍其中一些：
首先，初次见面时，你应该握手。其次，当你被邀请参加派对时，需要带一份礼物，并且务必准时或提前几分钟到达。第三，和他人一起用餐时，用筷子指着别人是不礼貌的；喝汤时也不要发出声响。
希望以上内容对你有帮助，如有更多问题，随时可以问我。
此致
李华

【写作要点】
格式规范：标准英文书信结构，符合中考评分要求；
逻辑清晰：开篇点明主题（介绍中国社交礼仪）；用 Firstly/Secondly/Thirdly 分点说明核心礼仪；结尾礼貌收尾并欢迎继续提问；
高分亮点：be supposed to do、be invited to、feel free to do；礼仪知识点准确；衔接词自然流畅。""",
            },
            {
                "id": "beijing-2021-s7",
                "title": "范文7 - 提高学习效率（译文+要点）",
                "body": """【英文范文】
High efficiency is important for a busy student. It is necessary for everyone to learn how to increase efficiency.
I have tried many ways to increase my efficiency since I was a middle school student. Before the beginning of a day, I always think about my goals and make a list of things I need to do. Then, I organize my to-do list to make an effective plan for the day. I always spend time doing important tasks first and make good use of the rest of the time.
By managing my time well, I have made progress. I usually finish my homework on time and still have time to relax myself. High efficiency and time management have helped me be successful in various areas of my life.

【中文翻译】
高效率对忙碌的学生来说至关重要，每个人都有必要学习如何提升效率。
从中学开始，我就尝试了很多提高效率的方法。每天开始前，我会明确自己的目标，列出待办事项；接着，我会整理清单，制定高效的当日计划；我总是优先完成重要任务，充分利用剩余时间。
通过良好的时间管理，我取得了很大进步：我通常能按时完成作业，还有时间放松自己。高效率和时间管理，帮助我在生活的各个领域都取得了成功。

【写作要点】
结构完整：开篇点明主题（效率的重要性）；主体介绍时间管理方法（列目标、列清单、定计划、先重后轻）；结尾说明收获并升华；
高分亮点：现在完成时与一般现在时搭配；从方法到结果的逻辑递进；主题积极，符合中考评分标准。""",
            },
        ],
    },
    {
        "id": "beijing-2020",
        "title": "2020年北京中考英语作文",
        "topics": """题目（二选一）
题目①：道歉邮件
假如你是李华，你不小心把Peter借给你的书弄丢了。为表达歉意，请用英语给他写一封邮件，告知此事，并提出弥补的办法。

提示词语：lose, make up（弥补）, buy, send
提示问题：What happened to the book? What will you do to make up for it?

题目②：积累知识
"不积跬步，无以至千里。"积累，有助于我们达成目标，实现梦想。某英文网站正在开展以"积累"为主题的征文活动。假如你是李华，请用英语写一篇短文投稿，谈谈你在积累知识方面做过什么，有什么收获。

提示词语：accumulate（积累）, read, keep, make progress
提示问题：What did you do to accumulate knowledge? What have you learned from doing so?""",
        "samples": [
            {
                "id": "beijing-2020-s1",
                "title": "范文1 - 道歉邮件",
                "body": """Dear Peter,

How are you getting on? I'm writing this email to say sorry because I lost the book which you lent to me last week.

I took your book to the park nearby to read this Saturday morning. At noon, I hurried home because my mom called me to have lunch. Not until I arrived home did I find that the book was lost. I went back to the park that afternoon, but unfortunately, the book was not there.

I want to make up for my mistake. I will buy the same one from the bookstore and send it to you by express mail. I hope you can forgive me. I'm really sorry for the trouble I've caused.

Looking forward to your reply.

Yours,
Li Hua""",
            },
            {
                "id": "beijing-2020-s2",
                "title": "范文2 - 积累知识",
                "body": """Without accumulating, we can hardly achieve anything. I'd like to share how I accumulate knowledge in my daily life.

First, I read books every day. I keep a habit of reading for at least 30 minutes before bed. I read storybooks, science magazines, and English articles. Second, I keep a notebook to write down important points and new words I learn. I often review these notes to strengthen my memory.

Through these methods, I've made great progress. Little by little, I've got a richer vocabulary and better understanding of the world. I've learned that accumulation requires patience and persistence. As the saying goes, "Rome wasn't built in a day." I will continue to accumulate knowledge and become a better person.""",
            },
            {
                "id": "beijing-2020-s3",
                "title": "范文3 - 道歉邮件",
                "body": """Dear Peter,

I'm really sorry to tell you that I accidentally lost the book you lent me. I feel terrible about this.

Yesterday, I took the book to a café to read while waiting for my friend. When I left, I was in such a hurry that I forgot to put it back in my bag. When I realized it and returned to the café, the book was gone. I asked the staff and looked everywhere, but couldn't find it.

To make up for this, I've already ordered a new copy online from a bookstore. It will arrive tomorrow, and I'll send it to you immediately. If the book is out of print, please let me know the price and I'll transfer the money to you. Again, I'm truly sorry for my carelessness.

Yours,
Li Hua""",
            },
            {
                "id": "beijing-2020-s4",
                "title": "范文4 - 积累知识",
                "body": """As an old saying goes, "A journey of a thousand miles begins with a single step." Accumulating knowledge is exactly like this journey.

To accumulate knowledge, I do several things regularly. Every morning, I spend 20 minutes reading English articles aloud. This helps me improve my pronunciation and accumulate new words. I also keep a diary in English, which allows me to practice writing and review what I've learned. Additionally, I often watch English movies and write down useful expressions.

Through consistent accumulation, I've made obvious progress in English. My reading speed has improved, and I can express myself more fluently. More importantly, I've realized that small efforts add up over time. Knowledge accumulation is a lifelong journey that I will continue to pursue.""",
            },
            {
                "id": "beijing-2020-s5",
                "title": "范文5 - 积累知识",
                "body": """Accumulating knowledge is like building a house brick by brick. Each piece of knowledge is like a brick, and the more bricks we have, the stronger our "house" becomes.

In my daily life, I accumulate knowledge in various ways. I read extensively - from history books to science magazines. When I encounter new words or interesting facts, I write them down in a special notebook and review them regularly. I also like to discuss what I've learned with my classmates, which helps deepen my understanding.

This habit has brought me many benefits. I've expanded my vocabulary significantly and can now read more challenging materials. I've also developed better thinking skills. Most importantly, I've learned that consistency is key - it's better to learn a little every day than to cram at the last minute. I will continue this journey of knowledge accumulation throughout my life.""",
            },
        ],
    },
]

# 语文卷（subject: chinese）；英语卷在 EXAMS 中于 main() 内统一标注 subject: english
CHINESE_EXAMS = [
    {
        "id": "beijing-cn-2025",
        "subject": "chinese",
        "title": "2025年北京中考语文作文",
        "topics": """题目（二选一）
题目一：请以"这样生活更健康"为题，写一篇作文。

题目二：请以"一堂科学课"为题，写一篇作文。

以下收录完整范文3篇。""",
        "samples": [
            {
                "id": "beijing-cn-2025-s1",
                "title": "范文1 - 《这样生活更健康》（阅读让生活更健康）",
                "body": """自十岁那年第一次翻开《城南旧事》起，阅读便成了我应对生活喧嚣的得力"武器"。阅读不仅是知识的汲取，更是心灵的滋养，是我追求健康生活的重要方式。

记得那次期中考试失利，心情跌落到谷底。同学的议论、老师的失望、父母的叹息，像一块巨石压得我喘不过气。回到家，我把自己关在房间里，目光无意间落在书架那本《飞鸟集》上。翻开书页，"生如夏花之绚烂"这句诗宛如一泓清泉，注入我干涸的心田，让紧绷的神经逐渐放松。泰戈尔的诗句像一位智慧的长者，轻轻拍着我的肩膀说：孩子，失败只是暂时的，生命的美好在于绽放的过程。

从此，每当我感到焦虑或迷茫，便会捧起一本书。周末的午后，我泡一杯茉莉花茶，坐在飘窗角落的软垫上。阳光斜洒在《人类群星闪耀时》的烫金封面上，茨威格描绘的历史画面如电影般在我眼前展开。拜占庭沦陷的惊心动魄，亨德尔重获灵感的狂喜，都让我沉浸其中，忘却了现实的烦恼。在阅读中，我学会了与自己和解，学会了在困境中保持希望。

阅读不仅调节我的情绪，更丰富了我的精神世界。通过书籍，我穿越时空与先贤对话，领略不同的文化与思想。我开始明白，健康不仅是身体的强健，更是心灵的丰盈。当我用阅读滋养心灵时，压力变成了动力，迷茫变成了清晰，焦虑变成了平静。

阅读宛如一场修行，教会我在焦虑时沉淀内心，在迷茫时冷静思考，让我学会与压力和解，为心灵守住一片净土。当书香融入我的日常生活，成为我生活的一部分时，我深切感受到，这样生活才是最健康、最有益的。

（全文约780字）""",
            },
            {
                "id": "beijing-cn-2025-s2",
                "title": "范文2 - 《一堂科学课》（煤炉里的科学）",
                "body": """奶奶用蜂窝煤炉做饭时，我旁观了一堂关于"燃烧"的科学课。

那天周末，我回到乡下奶奶家。午饭时分，奶奶准备生火做饭。我蹲在煤炉旁，好奇地观察着这一切。奶奶先将柴火点燃，放入炉膛，待火焰旺起来后，再加入蜂窝煤块。我仔细观察煤炉的结构：下面有通风口，能让空气进入，这就是提供氧气；奶奶会用柴火把煤块引燃，这是让温度达到着火点；而煤本身就是可燃物。当奶奶关上通风口，炉火就会慢慢变小，这是因为氧气不足，燃烧不充分。

"奶奶，为什么关上这个门，火就变小了呢？"我指着通风口问道。

"这叫风门，关上它，风就进不来了，火自然就小了。"奶奶一边回答，一边调整着风门的大小，炉火随之明暗变化。

这让我想起化学老师做的"蜡烛燃烧实验"——用玻璃杯罩住蜡烛，火焰很快就会熄灭。原来生活中的煤炉，就是一个大型的燃烧实验装置！燃烧的三个条件：可燃物、氧气、达到着火点的温度，在煤炉上体现得淋漓尽致。

这堂"煤炉课"教会我用科学眼光看生活。以前觉得"燃烧"是很简单的现象，可从煤炉的工作原理里，我找到了课本知识的对应点。我进一步思考：为什么蜂窝煤要制成蜂窝状？原来是为了增大与空气的接触面积，让燃烧更充分。为什么煤炉要设计烟囱？那是为了排出废气，促进空气流通……

后来在化学实验课上做"燃烧条件探究"实验时，我能很快理解实验设计的思路，因为煤炉的结构已经在我脑海里形成了清晰的画面。这种生活与课本的呼应，让学习变得更轻松。

更重要的是，这堂科学课让我明白：科学不是实验室里的神秘学问，而是生活中随处可见的能量变化。只要我们善于观察、勤于思考，生活处处都是科学课堂。

（全文约720字）""",
            },
            {
                "id": "beijing-cn-2025-s3",
                "title": "范文3 - 《这样生活更健康》（规律作息）",
                "body": """曾经的我是个"夜猫子"，熬夜追剧、打游戏是家常便饭。每天晚上不到凌晨绝不睡觉，早上则赖到最后一刻才起床。久而久之，身体发出了警报：上课走神、记忆力下降、脸上冒痘、情绪烦躁。直到一次体检，医生严肃地告诉我：长期熬夜已经严重影响了我的健康，必须立即调整。

我决定改变，从规律作息开始。首先，我给自己定下"铁律"：每天晚上十点半准时上床，早上六点半起床。起初的几天异常艰难，躺在床上辗转反侧，脑子里还想着未看完的剧情。但我咬牙坚持，睡前不看手机，改成听轻音乐或阅读纸质书。渐渐地，生物钟开始调整，到了十点半自然犯困，早上也能精神饱满地醒来。

规律作息带来的变化是显著的。首先是精神面貌的改善：不再昏昏沉沉，眼睛明亮有神，上课注意力集中了，学习效率大幅提高。其次是身体素质的提升：坚持早睡早起三个月后，困扰我多时的痘痘消失了，皮肤变得光滑，连感冒都少了。最重要的是情绪的稳定：充足的睡眠让我心态平和，不再因为小事暴怒或沮丧。

配合规律作息，我还养成了运动的习惯。每天早起后，我会做十分钟的拉伸运动；放学后跑步半小时或打羽毛球。运动让我释放压力，身体也更加强壮。以前爬三层楼就气喘吁吁，现在参加校运会的800米比赛都能拿到名次。

健康的生活还需要良好的心态。我学会了管理情绪，遇到挫折不再抱怨，而是积极寻找解决办法；感到压力时，会通过听音乐、写日记来放松自己。周末，我会和朋友去公园散步，呼吸新鲜空气，感受大自然的美好。

如今，规律作息已经成为我生活的一部分。我深刻体会到：健康不是一蹴而就的，而是日复一日的坚持。这样生活，让我的身心都更加健康，也让我以更饱满的热情迎接每一天的挑战。

（全文约750字）""",
            },
        ],
    },
] + CHINESE_EXAMS_EXTRA


def main():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    for e in EXAMS:
        e.setdefault("subject", "english")
    combined = EXAMS + CHINESE_EXAMS
    order = [
        "beijing-2025",
        "beijing-cn-2025",
        "beijing-2024",
        "beijing-cn-2024",
        "beijing-2023",
        "beijing-cn-2023",
        "beijing-2022",
        "beijing-cn-2022",
        "beijing-2021",
        "beijing-cn-2021",
        "beijing-2020",
        "beijing-cn-2020",
    ]
    by_id = {e["id"]: e for e in combined}
    exams_ordered = [by_id[i] for i in order if i in by_id]
    extras = [e for e in combined if e["id"] not in order]
    payload = {"version": 1, "exams": exams_ordered + extras}
    OUT.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {OUT}")
    js_out = ROOT / "data" / "essays-data.js"
    js_out.write_text(
        "window.ESSAYS_DATA = " + json.dumps(payload, ensure_ascii=False) + ";\n",
        encoding="utf-8",
    )
    print(f"Wrote {js_out}")


if __name__ == "__main__":
    main()
