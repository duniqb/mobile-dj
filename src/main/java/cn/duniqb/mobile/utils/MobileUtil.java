package cn.duniqb.mobile.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.security.Security;

/**
 * 项目工具类
 *
 * @author duniqb
 */

public class MobileUtil {
    /**
     * MD5 加密
     *
     * @param key
     * @return
     */
    public static String MD5(String key) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解密微信开放数据
     *
     * @param session_key
     * @param iv
     * @param encryptData
     * @return
     */
    public static String decrypt(String session_key, String iv, String encryptData) {
        String decryptString = "";
        init();
        byte[] sessionKeyByte = Base64.decodeBase64(session_key);
        byte[] ivByte = Base64.decodeBase64(iv);
        byte[] encryptDataByte = Base64.decodeBase64(encryptData);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key key = new SecretKeySpec(sessionKeyByte, "AES");
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("AES");
            algorithmParameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameters);
            byte[] bytes = cipher.doFinal(encryptDataByte);
            decryptString = new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptString;
    }

    private static boolean hasInit = false;

    private static void init() {
        if (hasInit) {
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        hasInit = true;
    }

    public static void main(String[] args) {
        String session_key = "HUN20o4S1/HAxsJFbnE6oQ==";
        String iv = "JCblGOuozorIGmy82boBKg==";
        String encryptData = "gTcbqWqG/o7gfE4JBLBV3+dCHvqcIpmrAINX19rB4zWLDyQwUPNNiUhm0FJNUAtX3wvnRsJNmxWOKP4PIoQnL1nmtqPT+qYZ8j+FtfHnveHDPrt3zzVJEaEmaHOYVrsGAXrIBZ/8QRC/e0Gi0e9lJupUKhebFkUwaf5xMBo7/hn+VOIxfEiCHE/KZ9WncP+fDvFiHPw4VWe+YoqPl541USMoqSEJxefOGLL13SvUh0bwAMQHdbUyrUu7foIf5wCewBwK+8j44alNPOPuJL9TD9YBdtlf8ZaNe5a2oUEfXjGfkM5V92o2yv/O8dOfyUmxJWk9NdyPNuEYOgOzUxVa2z/E41LkWKQltxCdPGYRY8yqEnpEX5FO7PwgQJZHMjtCu/KBjNvgwO2Jy/tJ2SeeR6EW2kN1G8hyorhfUtP2jS2g/svz/NRplBM7WsgrpSg/+hRCL4a/pKONg4vCuPO+/TDvfS8fryEzUJewZiknNsM=";
        String decrypt = decrypt(session_key, iv, encryptData);
        System.out.println(decrypt);
    }
}
