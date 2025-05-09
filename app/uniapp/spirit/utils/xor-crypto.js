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

function stringToBytes(str) {
  const bytes = [];
  for (let i = 0; i < str.length; i++) {
    const code = str.charCodeAt(i);
    if (code < 0x80) {
      bytes.push(code);
    } else if (code < 0x800) {
      bytes.push(0xc0 | (code >> 6));
      bytes.push(0x80 | (code & 0x3f));
    } else {
      bytes.push(0xe0 | (code >> 12));
      bytes.push(0x80 | ((code >> 6) & 0x3f));
      bytes.push(0x80 | (code & 0x3f));
    }
  }
  return new Uint8Array(bytes);
}

function bytesToString(bytes) {
  let str = '';
  let i = 0;
  while (i < bytes.length) {
    const byte1 = bytes[i++];
    if (byte1 < 0x80) {
      str += String.fromCharCode(byte1);
    } else if (byte1 >= 0xc0 && byte1 < 0xe0) {
      const byte2 = bytes[i++];
      str += String.fromCharCode(((byte1 & 0x1f) << 6) | (byte2 & 0x3f));
    } else {
      const byte2 = bytes[i++];
      const byte3 = bytes[i++];
      str += String.fromCharCode(
        ((byte1 & 0x0f) << 12) |
        ((byte2 & 0x3f) << 6) |
        (byte3 & 0x3f)
      );
    }
  }
  return str;
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
