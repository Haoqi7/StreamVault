// Base64 编码（Uint8Array -> base64 字符串）
function bytesToBase64(bytes) {
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return base64Encode(binary);
}

// Base64 解码（base64 字符串 -> Uint8Array）
function base64ToBytes(base64) {
  const binary = base64Decode(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes;
}

// 纯 JS 实现 Base64 编码（兼容小程序）
function base64Encode(str) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  let output = '';
  let i = 0;

  while (i < str.length) {
    const c1 = str.charCodeAt(i++);
    const c2 = str.charCodeAt(i++);
    const c3 = str.charCodeAt(i++);

    const e1 = c1 >> 2;
    const e2 = ((c1 & 3) << 4) | (c2 >> 4);
    const e3 = isNaN(c2) ? 64 : ((c2 & 15) << 2) | (c3 >> 6);
    const e4 = isNaN(c3) ? 64 : c3 & 63;

    output += chars.charAt(e1) + chars.charAt(e2) + chars.charAt(e3) + chars.charAt(e4);
  }

  return output;
}

function base64Decode(input) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  let str = '';
  let i = 0;

  input = input.replace(/[^A-Za-z0-9+/=]/g, '');

  while (i < input.length) {
    const e1 = chars.indexOf(input.charAt(i++));
    const e2 = chars.indexOf(input.charAt(i++));
    const e3 = chars.indexOf(input.charAt(i++));
    const e4 = chars.indexOf(input.charAt(i++));

    const c1 = (e1 << 2) | (e2 >> 4);
    const c2 = ((e2 & 15) << 4) | (e3 >> 2);
    const c3 = ((e3 & 3) << 6) | e4;

    str += String.fromCharCode(c1);
    if (e3 !== 64) str += String.fromCharCode(c2);
    if (e4 !== 64) str += String.fromCharCode(c3);
  }

  return str;
}

// 字符串转 Uint8Array
function stringToBytes(str) {
  const encoder = new TextEncoder();
  return encoder.encode(str);
}

// Uint8Array 转字符串
function bytesToString(bytes) {
  const decoder = new TextDecoder();
  return decoder.decode(bytes);
}

// XOR 加密核心逻辑
function xorBytes(dataBytes, keyBytes) {
  const result = new Uint8Array(dataBytes.length);
  for (let i = 0; i < dataBytes.length; i++) {
    result[i] = dataBytes[i] ^ keyBytes[i % keyBytes.length];
  }
  return result;
}

// 加密函数：输入明文字符串，输出 Base64 编码字符串
function encrypt(plainText, key) {
  const dataBytes = stringToBytes(plainText);
  const keyBytes = stringToBytes(key);
  const encryptedBytes = xorBytes(dataBytes, keyBytes);
  return bytesToBase64(encryptedBytes);
}

// 解密函数：输入 Base64 编码字符串，输出明文
function decrypt(cipherBase64, key) {
  const encryptedBytes = base64ToBytes(cipherBase64);
  const keyBytes = stringToBytes(key);
  const decryptedBytes = xorBytes(encryptedBytes, keyBytes);
  return bytesToString(decryptedBytes);
}

// 导出模块
export default {
  encrypt,
  decrypt
}
