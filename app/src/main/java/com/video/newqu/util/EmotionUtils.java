package com.video.newqu.util;

import com.video.newqu.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.video.newqu.R.drawable.emoji_56;
import static com.video.newqu.R.drawable.emoji_57;
import static com.video.newqu.R.drawable.emoji_58;
import static com.video.newqu.R.drawable.emoji_59;
import static com.video.newqu.R.drawable.emoji_60;
import static com.video.newqu.R.drawable.emoji_61;
import static com.video.newqu.R.drawable.emoji_62;
import static com.video.newqu.R.drawable.emoji_63;

/**
 * TinyHung@outlook.com
 * 2017/6/28 9:21
 * 对表情的封装
 */
public class EmotionUtils implements Serializable {

    public static Map<String, Integer> emojiMap;

    static {
        emojiMap = new HashMap<String, Integer>();
        emojiMap.put("[呵呵]", R.drawable.emoji_1);
        emojiMap.put("[嘻嘻]", R.drawable.emoji_2);
        emojiMap.put("[哈哈]", R.drawable.emoji_3);
        emojiMap.put("[爱你]", R.drawable.emoji_4);
        emojiMap.put("[挖鼻屎]", R.drawable.emoji_5);
        emojiMap.put("[吃惊]", R.drawable.emoji_6);
        emojiMap.put("[晕]", R.drawable.emoji_7);
        emojiMap.put("[泪]", R.drawable.emoji_8);
        emojiMap.put("[馋嘴]", R.drawable.emoji_9);
        emojiMap.put("[抓狂]", R.drawable.emoji_10);
        emojiMap.put("[哼]", R.drawable.emoji_11);
        emojiMap.put("[可爱]", R.drawable.emoji_12);
        emojiMap.put("[怒]", R.drawable.emoji_13);
        emojiMap.put("[汗]", R.drawable.emoji_14);
        emojiMap.put("[害羞]", R.drawable.emoji_15);
        emojiMap.put("[睡觉]", R.drawable.emoji_16);
        emojiMap.put("[钱]", R.drawable.emoji_17);
        emojiMap.put("[偷笑]", R.drawable.emoji_18);
        emojiMap.put("[笑cry]", R.drawable.emoji_19);
        emojiMap.put("[狗]", R.drawable.emoji_20);
        emojiMap.put("[喵星人]", R.drawable.emoji_21);
        emojiMap.put("[酷]", R.drawable.emoji_22);
        emojiMap.put("[衰]", R.drawable.emoji_23);
        emojiMap.put("[闭嘴]", R.drawable.emoji_24);
        emojiMap.put("[鄙视]", R.drawable.emoji_25);
        emojiMap.put("[花心]", R.drawable.emoji_26);
        emojiMap.put("[鼓掌]", R.drawable.emoji_27);
        emojiMap.put("[悲伤]", R.drawable.emoji_28);
        emojiMap.put("[思考]", R.drawable.emoji_29);
        emojiMap.put("[生病]", R.drawable.emoji_30);
        emojiMap.put("[亲亲]", R.drawable.emoji_31);
        emojiMap.put("[怒骂]", R.drawable.emoji_32);
        emojiMap.put("[太开心]", R.drawable.emoji_33);
        emojiMap.put("[懒得理你]", R.drawable.emoji_34);
        emojiMap.put("[右哼哼]", R.drawable.emoji_35);
        emojiMap.put("[左哼哼]", R.drawable.emoji_36);
        emojiMap.put("[嘘]", R.drawable.emoji_37);
        emojiMap.put("[委屈]", R.drawable.emoji_38);
        emojiMap.put("[吐]", R.drawable.emoji_39);
        emojiMap.put("[可怜]", R.drawable.emoji_40);
        emojiMap.put("[打哈气]", R.drawable.emoji_41);
        emojiMap.put("[挤眼]", R.drawable.emoji_42);
        emojiMap.put("[失望]", R.drawable.emoji_43);
        emojiMap.put("[顶]", R.drawable.emoji_44);
        emojiMap.put("[疑问]", R.drawable.emoji_45);
        emojiMap.put("[困]", R.drawable.emoji_46);
        emojiMap.put("[感冒]", R.drawable.emoji_47);
        emojiMap.put("[拜拜]", R.drawable.emoji_48);
        emojiMap.put("[黑线]", R.drawable.emoji_49);
        emojiMap.put("[阴险]", R.drawable.emoji_50);
        emojiMap.put("[打脸]", R.drawable.emoji_51);
        emojiMap.put("[傻眼]", R.drawable.emoji_52);
        emojiMap.put("[猪头]", R.drawable.emoji_53);
        emojiMap.put("[熊猫]", R.drawable.emoji_54);
        emojiMap.put("[兔子]", R.drawable.emoji_55);
        emojiMap.put("[奥特曼]", R.drawable.emoji_56);
        emojiMap.put("[囧囧]", R.drawable.emoji_57);
        emojiMap.put("[肥皂]", R.drawable.emoji_58);
        emojiMap.put("[秋波男]", R.drawable.emoji_59);
        emojiMap.put("[秋波女]", R.drawable.emoji_60);
        emojiMap.put("[神兽]", R.drawable.emoji_61);
        emojiMap.put("[热气球]", R.drawable.emoji_62);
        emojiMap.put("[鄙视你]", R.drawable.emoji_63);
        emojiMap.put("[没问题]", R.drawable.emoji_64);
        emojiMap.put("[点赞]", R.drawable.emoji_65);
        emojiMap.put("[示威]", R.drawable.emoji_66);
        emojiMap.put("[勾引]", R.drawable.emoji_67);
        emojiMap.put("[OK]", R.drawable.emoji_68);
        emojiMap.put("[加油]", R.drawable.emoji_69);
        emojiMap.put("[你不行]", R.drawable.emoji_70);
        emojiMap.put("[握手]", R.drawable.emoji_71);
        emojiMap.put("[耶]", R.drawable.emoji_72);
        emojiMap.put("[看好你]", R.drawable.emoji_73);
        emojiMap.put("[有礼了]", R.drawable.emoji_74);
        emojiMap.put("[萌娃_给力]", R.drawable.emoji_75);
        emojiMap.put("[萌娃_互粉]", R.drawable.emoji_76);
        emojiMap.put("[萌娃_囧]", R.drawable.emoji_77);
        emojiMap.put("[萌娃_萌]", R.drawable.emoji_78);
        emojiMap.put("[萌娃_什马]", R.drawable.emoji_79);
        emojiMap.put("[萌娃_威武]", R.drawable.emoji_80);
        emojiMap.put("[萌娃_双喜]", R.drawable.emoji_81);
        emojiMap.put("[萌娃_织]", R.drawable.emoji_82);
        emojiMap.put("[萌娃_爱心]", R.drawable.emoji_83);
        emojiMap.put("[萌娃_悲催]", R.drawable.emoji_84);
        emojiMap.put("[萌娃_崩溃]", R.drawable.emoji_85);
        emojiMap.put("[萌娃_别烦我]", R.drawable.emoji_86);
        emojiMap.put("[萌娃_不好意思]", R.drawable.emoji_87);
        emojiMap.put("[萌娃_不想上班]", R.drawable.emoji_88);
        emojiMap.put("[萌娃_得意的笑]", R.drawable.emoji_89);
        emojiMap.put("[萌娃_费劲]", R.drawable.emoji_90);
        emojiMap.put("[萌娃_好爱你]", R.drawable.emoji_91);
        emojiMap.put("[萌娃_好棒]", R.drawable.emoji_92);
        emojiMap.put("[萌娃_好囧]", R.drawable.emoji_93);
        emojiMap.put("[萌娃_笑哈哈]", R.drawable.emoji_94);
        emojiMap.put("[萌娃_羞嗒嗒]", R.drawable.emoji_95);
        emojiMap.put("[萌娃_许愿]", R.drawable.emoji_96);
        emojiMap.put("[萌娃_有压力]", R.drawable.emoji_97);
        emojiMap.put("[萌娃_赞]", R.drawable.emoji_98);
        emojiMap.put("[萌娃_震惊]", R.drawable.emoji_99);
        emojiMap.put("[萌娃_转发]", R.drawable.emoji_100);
        emojiMap.put("[萌娃_蛋糕]", R.drawable.emoji_101);
        emojiMap.put("[萌娃_飞机]", R.drawable.emoji_102);
        emojiMap.put("[萌娃_干杯]", R.drawable.emoji_103);
        emojiMap.put("[萌娃_话筒]", R.drawable.emoji_104);
        emojiMap.put("[萌娃_蜡烛]", R.drawable.emoji_105);
        emojiMap.put("[萌娃_礼物]", R.drawable.emoji_106);
        emojiMap.put("[萌娃_绿丝带]", R.drawable.emoji_107);
        emojiMap.put("[萌娃_围脖]", R.drawable.emoji_108);
        emojiMap.put("[萌娃_围观]", R.drawable.emoji_109);
        emojiMap.put("[萌娃_音乐]", R.drawable.emoji_110);
        emojiMap.put("[萌娃_照相机]", R.drawable.emoji_111);
        emojiMap.put("[萌娃_闹钟]", R.drawable.emoji_112);
        emojiMap.put("[萌娃_浮云]", R.drawable.emoji_113);
        emojiMap.put("[萌娃_沙尘暴]", R.drawable.emoji_114);
        emojiMap.put("[萌娃_太阳]", R.drawable.emoji_115);
        emojiMap.put("[萌娃_微风]", R.drawable.emoji_116);
        emojiMap.put("[萌娃_鲜花]", R.drawable.emoji_117);
        emojiMap.put("[萌娃_下雨]", R.drawable.emoji_118);
        emojiMap.put("[萌娃_月亮]", R.drawable.emoji_119);
        emojiMap.put("[萌娃_好喜欢]", R.drawable.emoji_120);
        emojiMap.put("[萌娃_Hold得住]", R.drawable.emoji_121);
        emojiMap.put("[萌娃_杰克逊]", R.drawable.emoji_122);
        emojiMap.put("[萌娃_拒绝]", R.drawable.emoji_123);
        emojiMap.put("[萌娃_冒汗]", R.drawable.emoji_124);
        emojiMap.put("[萌娃_扣鼻屎]", R.drawable.emoji_125);
        emojiMap.put("[萌娃_雷锋]", R.drawable.emoji_126);
        emojiMap.put("[萌娃_泪流满面]", R.drawable.emoji_127);
        emojiMap.put("[萌娃_玫瑰]", R.drawable.emoji_128);
        emojiMap.put("[萌娃_欧耶]", R.drawable.emoji_129);
        emojiMap.put("[萌娃_霹雳]", R.drawable.emoji_130);
        emojiMap.put("[萌娃_俏俏]", R.drawable.emoji_131);
        emojiMap.put("[萌娃_丘比特]", R.drawable.emoji_132);
        emojiMap.put("[萌娃_求关注]", R.drawable.emoji_133);
        emojiMap.put("[萌娃_群体围观]", R.drawable.emoji_134);
        emojiMap.put("[萌娃_偷乐]", R.drawable.emoji_135);
        emojiMap.put("[萌娃_推荐]", R.drawable.emoji_136);
        emojiMap.put("[萌娃_想一想]", R.drawable.emoji_137);
        emojiMap.put("[萌娃_困死了]", R.drawable.emoji_138);
        emojiMap.put("[新趣小视频]", R.drawable.emoji_139);
    }

    public static int getImgByName(String imgName) {
        Integer integer = emojiMap.get(imgName);
        return integer == null ? -1 : integer;
    }
}
