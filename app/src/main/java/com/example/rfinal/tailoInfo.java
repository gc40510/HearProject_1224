package com.example.rfinal;

import java.util.HashMap;
import java.util.Map;

public class tailoInfo {
    private static Map<String, String[]> taiLoDetails;

    static {
        taiLoDetails = new HashMap<>();

        // 聲母發音技巧
        taiLoDetails.put("p", new String[]{"雙唇音", "雙唇緊閉後瞬間放開，無明顯氣流，發音近似注音符號ㄅ"});
        taiLoDetails.put("ph", new String[]{"雙唇音", "雙唇緊閉後放開，同時強力送氣，發音近似注音符號ㄆ"});
        taiLoDetails.put("b", new String[]{"雙唇音", "雙唇緊閉後放開，聲帶震動，發音近似注音符號ㄅ"});
        taiLoDetails.put("m", new String[]{"雙唇音", "雙唇緊閉，氣流通過鼻腔，發音近似注音符號ㄇ"});
        taiLoDetails.put("t", new String[]{"舌尖音", "舌尖抵住上齒齦後放開，無明顯氣流，發音近似注音符號ㄉ"});
        taiLoDetails.put("th", new String[]{"舌尖音", "舌尖抵住上齒齦後放開，強力送氣，發音近似注音符號ㄊ"});
        taiLoDetails.put("n", new String[]{"舌尖音", "舌尖抵住上齒齦，氣流經鼻腔，發音近似注音符號ㄋ"});
        taiLoDetails.put("l", new String[]{"舌尖音", "舌尖輕觸上齒齦，氣流從舌側流出，發音近似注音符號ㄌ"});
        taiLoDetails.put("k", new String[]{"舌根音", "舌根抵住軟顎後放開，無明顯氣流，發音近似注音符號ㄍ"});
        taiLoDetails.put("kh", new String[]{"舌根音", "舌根抵住軟顎後放開，強力送氣，發音近似注音符號ㄎ"});
        taiLoDetails.put("g", new String[]{"舌根音", "舌根抵住軟顎後放開，聲帶震動，發音近似注音符號ㄍ"});
        taiLoDetails.put("ng", new String[]{"舌根音", "舌根貼近軟顎，氣流經鼻腔"});
        taiLoDetails.put("ts", new String[]{"舌尖音", "舌尖抵住上齒齦後放開，氣流摩擦產生聲音，發音近似注音符號ㄗ"});
        taiLoDetails.put("tsh", new String[]{"舌尖音", "舌尖抵住上齒齦後放開，強力送氣，氣流摩擦產生聲音，發音近似注音符號ㄘ"});
        taiLoDetails.put("s", new String[]{"舌尖音", "舌尖接近齒齦，氣流摩擦產生聲音"});
        taiLoDetails.put("h", new String[]{"喉音", "聲帶打開，氣流從喉部呼出，發音近似注音符號ㄏ"});
        taiLoDetails.put("j", new String[]{"舌尖音", "舌尖接近齒齦，聲帶震動，氣流摩擦產生聲音"});

        // 單元音（純母音）
        taiLoDetails.put("a", new String[]{"單元音", "嘴巴張大，舌頭放鬆，發「啊」音，發音近似注音符號ㄚ"});
        taiLoDetails.put("i", new String[]{"單元音", "舌尖抬高，嘴形扁平，發「衣」音，發音近似注音符號一"});
        taiLoDetails.put("u", new String[]{"單元音", "嘴唇圓起，舌根抬高，發「屋」音，發音近似注音符號ㄨ"});
        taiLoDetails.put("e", new String[]{"單元音", "嘴巴微開，舌面向前抬，發「ㄝ」音，發音近似注音符號ㄝ"});
        taiLoDetails.put("o", new String[]{"單元音", "嘴巴圓，舌根後縮，發「喔」音，發音近似注音符號ㄜ"});
        taiLoDetails.put("oo", new String[]{"單元音", "嘴巴較放鬆的「o」音，類似台語的「𠕇」，發音近似注音符號ㄛ"});

        // 複元音（雙母音）
        taiLoDetails.put("ai", new String[]{"複元音", "先發「a」，再滑向「i」"});
        taiLoDetails.put("au", new String[]{"複元音", "先發「a」，再滑向「u」，嘴唇圓起"});
        taiLoDetails.put("ia", new String[]{"複元音", "先發「i」，再滑向「a」"});
        taiLoDetails.put("iu", new String[]{"複元音", "先發「i」，再滑向「u」，嘴唇收圓"});
        taiLoDetails.put("io", new String[]{"複元音", "先發「i」，再滑向「o」"});
        taiLoDetails.put("ua", new String[]{"複元音", "嘴唇先圓起發「u」，再滑向「a」"});
        taiLoDetails.put("ui", new String[]{"複元音", "嘴唇圓起發「u」，再滑向「i」"});
        taiLoDetails.put("ue", new String[]{"複元音", "嘴唇先圓起發「u」，再滑向「e」"});
        taiLoDetails.put("iau", new String[]{"複元音", "i → a → u，嘴唇從展開到圓起"});
        taiLoDetails.put("uai", new String[]{"複元音", "u → a → i，嘴唇從圓形到展開"});

        // 鼻化元音
        taiLoDetails.put("ann", new String[]{"鼻化元音", "「a」發音時增加鼻音"});
        taiLoDetails.put("inn", new String[]{"鼻化元音", "「i」發音時增加鼻音"});
        taiLoDetails.put("enn", new String[]{"鼻化元音", "「e」發音時增加鼻音"});
        taiLoDetails.put("onn", new String[]{"鼻化元音", "「o」發音時增加鼻音"});
        taiLoDetails.put("ainn", new String[]{"鼻化元音", "「ai」發鼻音"});
        taiLoDetails.put("aunn", new String[]{"鼻化元音", "「au」發鼻音"});
        taiLoDetails.put("iann", new String[]{"鼻化元音", "「ia」發鼻音"});
        taiLoDetails.put("iunn", new String[]{"鼻化元音", "「iu」發鼻音"});
        taiLoDetails.put("uinn", new String[]{"鼻化元音", "「ui」發鼻音"});
        taiLoDetails.put("iaunn", new String[]{"鼻化元音", "「iau」發鼻音"});
        taiLoDetails.put("uann", new String[]{"鼻化元音", "「ua」發鼻音"});
        taiLoDetails.put("uainn", new String[]{"鼻化元音", "「uai」發鼻音"});

        // 鼻音韻母
        taiLoDetails.put("am", new String[]{"鼻音韻母", "「a」發音時，嘴巴微張，氣流通過鼻腔"});
        taiLoDetails.put("im", new String[]{"鼻音韻母", "「i」發音時，氣流通過鼻腔"});
        taiLoDetails.put("om", new String[]{"鼻音韻母", "「o」發音時，氣流通過鼻腔"});
        taiLoDetails.put("iam", new String[]{"鼻音韻母", "「ia」發音時，氣流通過鼻腔"});
        taiLoDetails.put("ang", new String[]{"鼻音韻母", "「a」+「ng」發音，舌根貼軟顎"});
        taiLoDetails.put("ing", new String[]{"鼻音韻母", "「i」+「ng」發音"});
        taiLoDetails.put("ong", new String[]{"鼻音韻母", "「o」+「ng」發音"});

        // 入聲韻母（-h結尾）
        taiLoDetails.put("ah", new String[]{"入聲韻母", "像「a」，但聲門收緊，短促收尾"});
        taiLoDetails.put("ih", new String[]{"入聲韻母", "像「i」，但聲門閉鎖"});
        taiLoDetails.put("uh", new String[]{"入聲韻母", "像「u」，但聲門閉鎖"});
        taiLoDetails.put("eh", new String[]{"入聲韻母", "像「e」，但聲門閉鎖"});
        taiLoDetails.put("oh", new String[]{"入聲韻母", "像「o」，但聲門閉鎖"});
        taiLoDetails.put("auh", new String[]{"入聲韻母", "「au」發完後聲門閉鎖"});
        taiLoDetails.put("iah", new String[]{"入聲韻母", "「ia」發完後聲門閉鎖"});
        taiLoDetails.put("iuh", new String[]{"入聲韻母", "「iu」發完後聲門閉鎖"});
        taiLoDetails.put("ioh", new String[]{"入聲韻母", "「io」發完後聲門閉鎖"});
        taiLoDetails.put("uah", new String[]{"入聲韻母", "「ua」發完後聲門閉鎖"});
        taiLoDetails.put("uih", new String[]{"入聲韻母", "「ui」發完後聲門閉鎖"});
        taiLoDetails.put("ueh", new String[]{"入聲韻母", "「ue」發完後聲門閉鎖"});
        taiLoDetails.put("iauh", new String[]{"入聲韻母", "「iau」發完後聲門閉鎖"});

        // 入聲韻母（-p結尾）
        taiLoDetails.put("ap", new String[]{"入聲韻母", "短促「ap」，嘴巴閉合"});
        taiLoDetails.put("ip", new String[]{"入聲韻母", "短促「ip」，嘴巴閉合"});
        taiLoDetails.put("op", new String[]{"入聲韻母", "短促「op」，嘴巴閉合"});
        taiLoDetails.put("iap", new String[]{"入聲韻母", "「ia」+「p」，短促收尾"});

        // 入聲韻母（-t結尾）
        taiLoDetails.put("at", new String[]{"入聲韻母", "短促「at」，舌尖碰上顎"});
        taiLoDetails.put("it", new String[]{"入聲韻母", "短促「it」，舌尖碰上顎"});
        taiLoDetails.put("ut", new String[]{"入聲韻母", "短促「ut」，舌尖碰上顎"});
        taiLoDetails.put("iat", new String[]{"入聲韻母", "「ia」+「t」，短促收尾"});
        taiLoDetails.put("uat", new String[]{"入聲韻母", "「ua」+「t」，短促收尾"});

        // 入聲韻母（-k結尾）
        taiLoDetails.put("ak", new String[]{"入聲韻母", "短促「ak」，舌根抬起"});
        taiLoDetails.put("ik", new String[]{"入聲韻母", "短促「ik」，舌根抬起"});
        taiLoDetails.put("ok", new String[]{"入聲韻母", "短促「ok」，舌根抬起"});
        taiLoDetails.put("iak", new String[]{"入聲韻母", "「ia」+「k」，短促收尾"});
        taiLoDetails.put("iok", new String[]{"入聲韻母", "「io」+「k」，短促收尾"});

        // 聲化韻母
        taiLoDetails.put("m", new String[]{"聲化韻母", "雙唇閉合，氣流經鼻腔發出"});
        taiLoDetails.put("ng", new String[]{"聲化韻母", "舌根貼軟顎，氣流經鼻腔發出"});
        // ================= 新增：聲調發音技巧 =================
        taiLoDetails.put("1", new String[]{"陰平", "高平調：保持高音，像國語第一聲「媽」"});
        taiLoDetails.put("2", new String[]{"陰上", "高降調：先高後低，像國語「馬」的開頭"});
        taiLoDetails.put("3", new String[]{"陰去", "低降調：像嘆氣「唉～」的下降感"});
        taiLoDetails.put("4", new String[]{"陰入", "短促中平：急促停頓，像「一」的結尾"});
        taiLoDetails.put("5", new String[]{"陽平", "中升調：由中音上揚，像國語「麻」但更明顯"});
        taiLoDetails.put("6", new String[]{"陽上", "低平調：低沉平穩，像「啊～」的低音"});
        taiLoDetails.put("7", new String[]{"陽去", "中平調：像國語輕聲「嗎」，但稍長"});
        taiLoDetails.put("8", new String[]{"陽入", "高短調：短促高音，像突然喊「喔！」"});
    }

    public static String[] getTaiLoDetails(String syllable) {
        return taiLoDetails.getOrDefault(syllable, new String[]{"未知", "無描述資訊"});
    }
}