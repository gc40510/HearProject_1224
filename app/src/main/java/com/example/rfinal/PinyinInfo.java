package com.example.rfinal;

import java.util.HashMap;
import java.util.Map;

public class PinyinInfo {
    private static Map<Character, String[]> pinyinDetails;

    static {
        pinyinDetails = new HashMap<>();
        pinyinDetails.put('ㄅ', new String[]{"雙唇", "嘴唇緊閉，舌頭不接觸上顎，發音時嘴唇爆開，像英文的 /b/。"});
        pinyinDetails.put('ㄆ', new String[]{"雙唇", "嘴唇緊閉，舌頭不接觸上顎，發音時氣流強烈地從雙唇爆發，像英文的 /p/。"});
        pinyinDetails.put('ㄇ', new String[]{"雙唇", "嘴唇輕閉，舌頭不接觸硬顎，發音時氣流通過鼻腔，類似英文的 /m/。"});
        pinyinDetails.put('ㄈ', new String[]{"上齒與下唇", "上齒輕咬住下唇，發音時氣流從牙齒和唇之間擠出，像英文的 /f/。"});
        pinyinDetails.put('ㄉ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時嘴巴微開，舌尖迅速離開齦部，像英文的 /d/。"});
        pinyinDetails.put('ㄊ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時氣流強烈的從舌尖附近的縫隙擠出，像英文的 /t/。"});
        pinyinDetails.put('ㄋ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，氣流從鼻腔發出，像英文的 /n/。"});
        pinyinDetails.put('ㄌ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時舌頭兩側稍微鬆開，讓氣流通過兩側，像英文的 /l/。"});
        pinyinDetails.put('ㄍ', new String[]{"舌根與軟顎", "舌根接觸軟顎，嘴巴微開，發音時氣流從舌根與軟顎之間爆發，像英文的 /g/。"});
        pinyinDetails.put('ㄎ', new String[]{"舌根與軟顎", "舌根接觸軟顎，嘴巴微開，發音時強烈的氣流從口腔內部爆發，像英文的 /k/。"});
        pinyinDetails.put('ㄏ', new String[]{"喉部", "舌頭放鬆，氣流從喉部發出，嘴巴微開，像英文的 /h/。"});
        pinyinDetails.put('ㄐ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，發音時舌頭迅速離開，氣流爆發，像英文的 /j/。"});
        pinyinDetails.put('ㄑ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，發音時需要強烈的氣流，嘴唇微圓，像英文的 /ch/。"});
        pinyinDetails.put('ㄒ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，嘴唇微微拉開，發音時氣流迅速擠出，像英文的 /sh/。"});
        pinyinDetails.put('ㄓ', new String[]{"舌尖與硬顎", "舌尖輕觸硬顎，氣流從舌頭兩側擠出，像英文的 /zh/。"});
        pinyinDetails.put('ㄔ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，發音時氣流從舌頭兩側強烈擠出，嘴型圓形，像英文的 /ch/。"});
        pinyinDetails.put('ㄕ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，嘴巴微微張開，發音時氣流從舌頭兩側流出，像英文的 /sh/。"});
        pinyinDetails.put('ㄖ', new String[]{"舌尖與硬顎", "舌尖接觸硬顎，舌尖稍微捲起，氣流從舌頭兩側流出，像英文的 /r/。"});
        pinyinDetails.put('ㄗ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時氣流從舌頭兩側流出，像英文的 /z/。"});
        pinyinDetails.put('ㄘ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時強烈的氣流從舌頭兩側擠出，像英文的 /ts/。"});
        pinyinDetails.put('ㄙ', new String[]{"舌尖與上齒齦", "舌尖輕觸上齒齦，發音時氣流從舌頭兩側流出，像英文的 /s/。"});
        pinyinDetails.put('ㄧ', new String[]{"舌面與硬顎", "舌面靠近硬顎，嘴巴微微展開，舌面保持平坦，像英文的 /i/。"});
        pinyinDetails.put('ㄨ', new String[]{"舌面與軟顎", "舌面靠近軟顎，嘴型圓形，發音時嘴巴微微張開，像英文的 /u/。"});
        pinyinDetails.put('ㄩ', new String[]{"舌面與硬顎", "舌面靠近硬顎，嘴唇圓形，發音時像英文的 /ü/。"});
        pinyinDetails.put('ㄚ', new String[]{"舌根與軟顎", "舌根放鬆，嘴巴大開，發音時嘴巴張開，像英文的 /a/。"});
        pinyinDetails.put('ㄛ', new String[]{"舌根與軟顎", "舌根抬起，嘴巴圓形，發音時嘴巴圓張，像英文的 /o/。"});
        pinyinDetails.put('ㄜ', new String[]{"舌面與硬顎", "舌面略微抬起，嘴巴微開，發音時嘴唇微微放鬆，像英文的 /e/。"});
        pinyinDetails.put('ㄝ', new String[]{"舌面與硬顎", "舌面輕觸硬顎，嘴巴微開，發音時嘴唇微微放鬆，像英文的 /ɛ/。"});
        pinyinDetails.put('ㄞ', new String[]{"舌面與硬顎", "舌面略微上升，嘴巴開口，發音時嘴巴開放，像英文的 /ai/。"});
        pinyinDetails.put('ㄟ', new String[]{"舌面與硬顎", "舌面稍微抬起，嘴巴微開，發音時嘴巴呈圓形，像英文的 /ei/。"});
        pinyinDetails.put('ㄠ', new String[]{"舌面與軟顎", "舌根抬起，嘴巴微圓，發音時嘴巴圓形，像英文的 /ao/。"});
        pinyinDetails.put('ㄡ', new String[]{"舌面與軟顎", "舌面接近軟顎，嘴巴圓形，發音時嘴巴圓張，像英文的 /ou/。"});
        pinyinDetails.put('ㄢ', new String[]{"舌面與硬顎", "舌面上升，嘴巴微開，發音時鼻腔共鳴，像英文的 /an/。"});
        pinyinDetails.put('ㄣ', new String[]{"舌面與硬顎", "舌面微抬，嘴巴微開，發音時氣流從鼻腔發出，像英文的 /en/。"});
        pinyinDetails.put('ㄤ', new String[]{"舌根與軟顎", "舌根抬起，嘴巴開放，發音時氣流從鼻腔發出，像英文的 /ang/。"});
        pinyinDetails.put('ㄥ', new String[]{"舌根與軟顎", "舌根抬起，嘴巴微開，發音時氣流從鼻腔發出，像英文的 /eng/。"});
        pinyinDetails.put('ㄦ', new String[]{"舌尖與硬顎", "舌尖略捲，嘴巴微開，發音時嘴型呈圓形，像英文的 /er/。"});
        // 添加其他符號...
    }

    public static String[] getPinyinDetails(char pinyin) {
        return pinyinDetails.getOrDefault(pinyin, new String[]{"未知", "無描述資訊"});
    }
}
