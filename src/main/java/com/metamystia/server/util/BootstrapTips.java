package com.metamystia.server.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;

public class BootstrapTips {
    static public String encodedSpecialTip = "SGFwcHkgYmlydGhkYXksIE15c3RpYSE=";

    static public String[] encodedTips = new String[] {
            "VGhpcyBpcyBqdXN0IGEgdGlwLi4u",
            "SnVzdCBhbm90aGVyIHRpcC4uLg==",
            "TmVlZCBhIGJyZWFrIGZyb20gbXkgY2hhdHRlcj8gSnVzdCBhZGQgIuKAlG5vLXRpcCIuLi4=",
            "UHJlc3MgQWx0K0Y0IHRvIG9wdGltaXplIG1lbW9yeS4uLg==",
            "SmF2YSAxNiArIDEgKyA0ID0gMjEh",
            "UG93ZXJlZCBieSBOZXR0eSE=",
            "UG93ZXJlZCBieSBCcmlnYWRpZXIh",
            "UG93ZXJlZCBieSBIRUFSVCE=",
            "QWxzbyB0cnkgVG91aG91IEJsb29taW5nIENoYW9zIDIh",
            "QWxzbyB0cnkgVGhyZWUgRmFpcmllcycgSG9wcGluJyBGbGFwcGluJyBHcmVhdCBKb3VybmV5IQ==",
            "QWxzbyB0cnkgTUNHZW5zb2t5byE=",
            "QmFkIE1ldGFNaWt1ISE=",
            "QWxzbyB0cnkgU2dyTXlzdGlhU2VydmVySiE=",
            "S3lvdWtvIElTIFlPVSE=",
            "Tk92ZXIgc2F5IG5vIHRvIHBPbmRhIQ==",
            "Tm8gcmljZSBpbiB0aGUgcmljZSBiYWxsPz8=",
            "U2VlIFlvdSBOZXh0IERyZWFtISBXYWl0LCAwIGluY29tZT8=",
            "JXMgW2lzL2FyZV0gbm8gbG9uZ2VyIHBvcHVsYXIu",
            "QklHIFNBTEU6IFJFSVNFTiAtIDUzMDAwMCB5ZW4=",
            "MzAlIG9mZiB0b2RheSE=",
            "Tm90IHN0ZWFsaW5nIGF0IGFsbCE=",
            "IlN0cm9uZyIgU2VsbCBBbmQgQnV5",
            "UGFuZWwgaXMgb3V0ZGF0ZWQ7IHlvdSBuZWVkIFBBTk5FTCE=",
            "R2lybHMgQXJlIE5vdyBQcmVwYXJpbmcuLi4=",
            "Tm8gdGlwIHRvZGF5LCBzb3JyeS4=",
            "QXlvJ3MgdGhlIEJFU1QhIChBeWEgYWxzbyA6UCk=",
            "YXNzZXJ0IFN1aWthLmV1cWFscygiQ2l0cnVsbHVzIGxhbmF0dXMiKTs=",
            "SGVscCBtZSwgRVJJTk5OTk4hIQ==",
            "V2luZG93cyA5IGlzIG5vdCBzdXBwb3J0ZWQh",
            "V29uZGVyaG9+IHkh",
            "SG91c3Rvbiwgd2UgaGF2ZSBhIHByb2JsZW0u",
            "QmVhdXRpZnVsIGlzIGJldHRlciB0aGFuIHVnbHksIGFuZCBleHBsaWNpdCBpcyBiZXR0ZXIgdGhhbiBpbXBsaWNpdC4=",
            "UW1GelpUWTBTVzVDWVhObE5qUWg=",
            "cWF6d3N4ZWRjcmZ2dGdieWhudWptaWssb2wucDsvWyddJw==",
            "T2theSwgMzYgaXMgZW5vdWdoLg=="
    };

    static public String decodeTip(String encodedTip) {
        return new String(Base64.getDecoder().decode(encodedTip), StandardCharsets.UTF_8);
    }

    static public String getTip() {
        if(enableSpecialTip()) return decodeTip(encodedSpecialTip);
        return rollOneTip();
    }

    static public boolean enableSpecialTip() {
        return Calendar.getInstance().get(Calendar.MONTH) == Calendar.JUNE && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 16;
    }

    static public String rollOneTip() {
        return decodeTip(encodedTips[(int)Math.floor(Math.random() * encodedTips.length)]);
    }
}
