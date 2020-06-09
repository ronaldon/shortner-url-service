package io.platformbuilders.shortenerurl.service.impl;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        HashCode hashString =
                Hashing.sipHash24().hashString(
                //Hashing.adler32().hashString(
           //Hashing.murmur3_32().hashString(
                "www.uol.com.br?ddda=sdasd"+System.nanoTime(), StandardCharsets.UTF_8);
        
        System.out.println(hashString.toString());
    }

}
