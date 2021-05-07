/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oneboot.core.utils;


import org.oneboot.core.lang.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音转换工具类。
 * 
 * <pre>
 *  实现拼音的转换。非中文的字符，原样返回。
 *  如：
 *      天府软件园  -->  tianfuruanjianyuan
 *      tianfu  -->  tianfu
 *      #$%     -->  #$%
 *  注意：
 *      在使用formatPinYinCharacter(char c) 方法时，如果传入的c不为中文，那么返回c
 * </pre>
 * 
 * @author shiqiao.pro
 * 
 */
public class PinyinFormatUtil {

    /**
     * 转换一个拼音字符。
     * 
     * @param c
     *            字符
     * @return 拼音
     */
    public static String formatPinYinCharacter(char c) {
        String[] pinyin = null;
        try {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            return null;
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null || pinyin.length == 0) {
            return String.valueOf(c);
        }

        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }

    /**
     * 转换一个拼音字符串。
     * 
     * @param str
     *            字符串
     * @return 拼音
     */
    public static String formatPinYinStr(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for (int i = 0; i < str.length(); ++i) {
            tempPinyin = formatPinYinCharacter(str.charAt(i));
            if (tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    /**
     * 转换一个拼音字符串，只返回汉字的首字母。
     * 
     * @param str
     * @return
     */
    public static String formatPinyinInitial(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for (int i = 0; i < str.length(); ++i) {
            tempPinyin = formatPinYinCharacter(str.charAt(i));
            if (StringUtils.isBlank(tempPinyin)) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(StringUtils.substring(tempPinyin, 0, 1));
            }
        }
        return sb.toString();
    }

}
