package org.xtimms.yomu.util;

import java.util.Random;

public class KaomojiUtil {

    private static final String[] ERROR_FACES = {
            "(￣ヘ￣)",
            "ヾ(`ヘ´)ﾉﾞ",
            "Σ(ಠ_ಠ)",
            "ಥ_ಥ",
            "(˘･_･˘)",
            "(；￣Д￣)",
            "(･Д･。)",
            "o(╥﹏╥)",
            "(◞ ‸ ◟ㆀ)",
            "(ᗒᗣᗕ)՞",
            "(-ω-、)",
            "(⋟﹏⋞)",
            "(ノ﹏ヽ)",
            "(T⌓T)",
            "(◕︿◕✿)",
            "⊙︿⊙"
    };

    public static String getRandomErrorFace() {
        return ERROR_FACES[new Random().nextInt(ERROR_FACES.length)];
    }

}
