package com.wxiang.jwt;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class JwtTest {

    private static long tokenExpiration = 1000 * 60 * 60 * 12;
    private static String tokenSignKey = "wxiangSignKey";

    @Test
    public void createdToken() {
        JwtBuilder jwtBuilder = Jwts.builder();
        // Jwt由三部分组成：Jwt头，有效载荷，签名哈希
        String jwtToken = jwtBuilder
                // 头
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")

                // 载荷：自定义信息
                .claim("nickname", "wxiang")
                .claim("avatar", "2.jpg")
                .claim("role", "admin")

                // 载荷：默认信息
                .setSubject("srb-wxiang")  // 令牌主题
                .setIssuer("wxiang")  // 签发者
                .setAudience("lenovo")  // 接收方
                .setIssuedAt(new Date())  // 令牌的签发时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))  // 令牌过期时间
                .setNotBefore(new Date(System.currentTimeMillis() + 1000 * 20))  // 令牌生效时间
                .setId(UUID.randomUUID().toString())  // jti jwt的唯一身份标识，用来回避重放攻击

                // 签名哈希
                .signWith(SignatureAlgorithm.HS256, tokenSignKey)

                // 组装jwt字符串
                .compact();

        System.out.println(jwtToken);
        /*
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9  头
        .eyJuaWNrbmFtZSI6Ind4aWFuZyIsImF2YXRhciI6IjIuanBnIiwicm9sZSI6ImFkbWlu   载荷
        Iiwic3ViIjoic3JiLXd4aWFuZyIsImlzcyI6Ind4aWFuZyIsImF1ZCI6Imxlbm92byIsI
        mlhdCI6MTY1NDg0OTQ1NywiZXhwIjoxNjU0ODkyNjU3LCJuYmYiOjE2NTQ4NDk0NzcsIm
        p0aSI6ImJmMzQ3ZTM0LTI3YjMtNDc5Zi1iMmEyLTNmOTg0MWUyYzYwZCJ9
        .mYaP62qaRhdfYS8i6b9OjcNYaFxBPcvdqqrM3lL0iLo   签名哈希

         */
    }

    @Test
    public void getTokenInfo() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaWNrbmFtZSI6Ind4aWFuZyIsImF2YXRhciI6IjIuanBnIiwicm9sZSI6ImFkbWluIiwic3ViIjoic3JiLXd4aWFuZyIsImlzcyI6Ind4aWFuZyIsImF1ZCI6Imxlbm92byIsImlhdCI6MTY1NDg0OTQ1NywiZXhwIjoxNjU0ODkyNjU3LCJuYmYiOjE2NTQ4NDk0NzcsImp0aSI6ImJmMzQ3ZTM0LTI3YjMtNDc5Zi1iMmEyLTNmOTg0MWUyYzYwZCJ9.mYaP62qaRhdfYS8i6b9OjcNYaFxBPcvdqqrM3lL0iLo";
        JwtParser parser = Jwts.parser();
        Jws<Claims> claimsJws = parser.setSigningKey(tokenSignKey).parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        String nickname = (String) claims.get("nickname");
        String avatar = (String) claims.get("avatar");
        String role = (String) claims.get("role");
        System.out.println(nickname);
        System.out.println(avatar);
        System.out.println(role);

        String id = claims.getId();
        System.out.println(id);
    }
}
