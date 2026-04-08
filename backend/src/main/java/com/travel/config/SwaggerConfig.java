package com.travel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 * 
 * 访问地址：
 * - Swagger UI: http://localhost:8080/doc.html
 * - OpenAPI: http://localhost:8080/v3/api-docs
 * 
 * 配置项说明：
 * - knife4j.enable=true  启用增强功能（分组、导出等）
 * - springdoc.swagger-ui.path=/swagger-ui.html  原生UI路径
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧旅游管理系统 API")
                        .version("1.0.0")
                        .description("适合冲优秀毕业设计 | Vue3 + Spring Boot 3 全栈项目\n\n" +
                                "## 接口认证\n" +
                                "所有需要认证的接口，请在请求头中添加：\n" +
                                "```\n" +
                                "Authorization: Bearer <your-jwt-token>\n" +
                                "```\n\n" +
                                "## 响应格式\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"code\": 200,\n" +
                                "  \"message\": \"success\",\n" +
                                "  \"data\": {}\n" +
                                "}\n" +
                                "```")
                        .contact(new Contact()
                                .name("旅游系统开发团队")
                                .email("admin@travel.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT认证令牌")));
    }
}
