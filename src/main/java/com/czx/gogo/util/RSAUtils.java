package com.czx.gogo.util;

import org.slf4j.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * SHA256withRSA
 *
 * @author zhumiao
 *
 */
public class RSAUtils {

    private static final Logger logger = LoggerFactory.getLogger(RSAUtils.class);

    // MAX_DECRYPT_BLOCK应等于密钥长度/8（1byte=8bit），所以当密钥位数为2048时，最大解密长度应为256.
    // 128 对应 1024，256对应2048
    private static final int KEYSIZE = 2048;

    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;

    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 256;

    // 不仅可以使用DSA算法，同样也可以使用RSA算法做数字签名
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";


    // 默认种子 请根据自己的需要定义
    public static final String DEFAULT_SEED = "xxxxxxxxxxxxxxxxxx";

    /**
     * 目前固定公钥、私钥，有需求再改动
     */
    public static String PUBLIC_KEY_LZ = "***************";
    public static String PRIVATE_KEY_LZ = "*******************************************=";

    public static final String PUBLIC_KEY = "PublicKey";
    public static final String PRIVATE_KEY = "PrivateKey";

    /**
     *
     * 生成密钥
     *
     * @param seed 种子
     *
     * @return 密钥对象
     * @throws Exception
     *
     */
    public static Map<String, Key> initKey(String seed) throws Exception {
        logger.info("生成密钥");
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        // 如果指定seed，那么secureRandom结果是一样的，所以生成的公私钥也永远不会变
        secureRandom.setSeed(seed.getBytes());
        // Modulus size must range from 512 to 1024 and be a multiple of 64
        keygen.initialize(KEYSIZE, secureRandom);
        KeyPair keys = keygen.genKeyPair();
        PrivateKey privateKey = keys.getPrivate();
        PublicKey publicKey = keys.getPublic();
        Map<String, Key> map = new HashMap<>(2);
        map.put(PUBLIC_KEY, publicKey);
        map.put(PRIVATE_KEY, privateKey);
        return map;
    }

    /**
     *
     * 生成默认密钥
     *
     *
     * @return 密钥对象
     * @throws Exception
     *
     */

    public static Map<String, Key> initKey() throws Exception {
        return initKey(DEFAULT_SEED);
    }

    /**
     *
     * 取得私钥
     *
     * @param keyMap
     *
     * @return
     * @throws Exception
     *
     */
    public static String getPrivateKey(Map<String, Key> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
//        return encryptBASE64(key.getEncoded()); // base64加密私钥
        return base64ToStr(key.getEncoded()); // base64加密私钥
    }

    private static String base64ToStr(byte[] encoded) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(encoded);
    }

    /**
     *
     * 取得公钥
     *
     * @param keyMap
     *
     * @return
     * @throws Exception
     *
     */
    public static String getPublicKey(Map<String, Key> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        return encryptBASE64(key.getEncoded()); // base64加密公钥
        return base64ToStr(key.getEncoded()); // base64加密公钥
    }

    /**
     *
     * 用私钥对信息进行数字签名
     *
     * @param data       加密数据
     *
     * @param privateKey 私钥-base64加密的
     *
     * @return
     *
     * @throws Exception
     *
     */
    public static String signByPrivateKey(byte[] data, String privateKey) throws Exception {
        logger.info("用私钥对信息进行数字签名");
        byte[] keyBytes = decryptBASE64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey priKey = factory.generatePrivate(keySpec);// 生成私钥
        // 用私钥对信息进行数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());

    }

    /**
     *
     * BASE64Encoder 加密
     *
     * @param data 要加密的数据
     *
     * @return 加密后的字符串
     *
     */
    private static String encryptBASE64(byte[] data) {
//      BASE64Encoder encoder = new BASE64Encoder();
//      String encode = encoder.encode(data);
//      return encode;
        return new String(Base64.encodeBase64(data));
    }

    private static byte[] decryptBASE64(String data) {
        // BASE64Decoder 每76个字符换行
//      BASE64Decoder decoder = new BASE64Decoder();
//      byte[] buffer = decoder.decodeBuffer(data);
//      return buffer;
        // codec 的 Base64 不换行
        return Base64.decodeBase64(data);
    }

    // TODO rsa 2 对比是否更优
    public static boolean verifyByPublicKey(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = decryptBASE64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(decryptBASE64(sign)); // 验证签名
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                    加密过程中的异常信息
     */
    public static String encryptByPublicKey(String str, String publicKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        logger.info(publicKey);
        logger.info("provider: {}", cipher.getProvider().getClass().getName());
        byte[] data = str.getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        logger.info(enBytes.length + "");
        String outStr = encryptBASE64(enBytes);
        return outStr;
    }

    /**
     * RSA私钥加密
     *
     * @param str       加密字符串
     * @param privateKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                    加密过程中的异常信息
     */
    public static String encryptByPrivateKey(String str, String privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // base64编码的公钥
        byte[] keyBytes = decryptBASE64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);

        byte[] data = str.getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        String outStr = encryptBASE64(enBytes);
        return outStr;
    }

    /**
     * 读取公钥
     *
     * @param publicKeyPath
     * @return
     */
    public static PublicKey readPublic(String publicKeyPath) {
        if (publicKeyPath != null) {
            try (FileInputStream bais = new FileInputStream(publicKeyPath)) {
                CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certificatefactory.generateCertificate(bais);
                return cert.getPublicKey();
            } catch (CertificateException e) {
                logger.error(e.getMessage(), e);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 读取私钥
     *
     * @param privateKeyPath
     * @return
     */
    public static PrivateKey readPrivate(String privateKeyPath, String privateKeyPwd) {
        if (privateKeyPath == null || privateKeyPwd == null) {
            return null;
        }
        try (InputStream stream = new FileInputStream(new File(privateKeyPath));) {
            // 获取JKS 服务器私有证书的私钥，取得标准的JKS的 KeyStore实例
            KeyStore store = KeyStore.getInstance("JKS");// JKS，二进制格式，同时包含证书和私钥，一般有密码保护；PKCS12，二进制格式，同时包含证书和私钥，一般有密码保护。
            // jks文件密码，根据实际情况修改
            store.load(stream, privateKeyPwd.toCharArray());
            // 获取jks证书别名
            Enumeration<String> en = store.aliases();
            String pName = null;
            while (en.hasMoreElements()) {
                String n = (String) en.nextElement();
                if (store.isKeyEntry(n)) {
                    pName = n;
                }
            }
            // 获取证书的私钥
            PrivateKey key = (PrivateKey) store.getKey(pName, privateKeyPwd.toCharArray());
            return key;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * RSA私钥解密
     *
     * @param encryStr   加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPrivateKey(String encryStr, String privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        // base64编码的私钥
        byte[] decoded = decryptBASE64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        logger.info(privateKey);
        logger.info("provider: {}" , cipher.getProvider().getClass().getName());
        // 64位解码加密后的字符串
        byte[] data = decryptBASE64(encryStr);
        logger.info(data.length + "");
        // 解密时超过128字节报错。为此采用分段解密的办法来解密
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
            sb.append(new String(doFinal));
        }
        return sb.toString();
    }

    /**
     * RSA公钥解密
     *
     * @param encryStr   加密字符串
     * @param publicKey 公钥
     * @return 铭文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPublicKey(String encryStr, String publicKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        // base64编码的私钥
        byte[] decoded = decryptBASE64(publicKey);
        RSAPublicKey priKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 64位解码加密后的字符串
        byte[] data = decryptBASE64(encryStr);
        // 解密时超过128字节报错。为此采用分段解密的办法来解密
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
            sb.append(new String(doFinal));
        }

        return sb.toString();
    }

    /**
     * 加密
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static String testEncrypt(String key,String data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException{
        byte[] decode = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey generatePrivate = kf.generatePrivate(pkcs8EncodedKeySpec);
        Cipher ci = Cipher.getInstance(KEY_ALGORITHM);
        ci.init(Cipher.ENCRYPT_MODE, generatePrivate);

        byte[] bytes = data.getBytes();
        int inputLen = bytes.length;
        int offLen = 0;//偏移量
        int i = 0;
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        while(inputLen - offLen > 0){
            byte [] cache;
            if(inputLen - offLen > 117){
                cache = ci.doFinal(bytes, offLen,117);
            }else{
                cache = ci.doFinal(bytes, offLen,inputLen - offLen);
            }
            bops.write(cache);
            i++;
            offLen = 117 * i;
        }
        bops.close();
        byte[] encryptedData = bops.toByteArray();
        String encodeToString = Base64.encodeBase64String(encryptedData);
        return encodeToString;
    }




    /**
     * 解密
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public static String testDecrypt(String key,String data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, IOException{
        byte[] decode = Base64.decodeBase64(key);
//      PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decode); //java底层 RSA公钥只支持X509EncodedKeySpec这种格式
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey generatePublic = kf.generatePublic(x509EncodedKeySpec);
        Cipher ci = Cipher.getInstance(KEY_ALGORITHM);
        ci.init(Cipher.DECRYPT_MODE,generatePublic);

        int inputLen = data.getBytes().length;
        byte[] bytes = data.getBytes();
        int offLen = 0;
        int i = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while(inputLen - offLen > 0){
            byte[] cache;
            if(inputLen - offLen > 128){
                cache = ci.doFinal(bytes,offLen,128);
            }else{
                cache = ci.doFinal(bytes,offLen,inputLen - offLen);
            }
            byteArrayOutputStream.write(cache);
            i++;
            offLen = 128 * i;

        }
        byteArrayOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return new String(byteArray);
    }
}
