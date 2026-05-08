package com.qindashuai.toolkit;

import com.qindashuai.toolkit.excel.ExcelColumn;
import com.qindashuai.toolkit.excel.ExcelExportUtil;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelExportUtilTest {

    private ExcelExportUtil excelExportUtil;

    @Data
    static class TestUser {
        @ExcelColumn(name = "用户ID", order = 1, width = 15)
        private Long id;

        @ExcelColumn(name = "用户名", order = 2, width = 20)
        private String username;

        @ExcelColumn(name = "年龄", order = 3, width = 10)
        private Integer age;

        @ExcelColumn(name = "创建时间", order = 4, width = 25, dateFormat = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;

        @ExcelColumn(name = "启用状态", order = 5, width = 10)
        private Boolean enabled;

        @ExcelColumn(name = "备注", order = 6, width = 30, defaultValue = "无")
        private String remark;
    }

    @Data
    static class EmptyEntity {
        private String field;
    }

    @BeforeEach
    void setUp() {
        excelExportUtil = new ExcelExportUtil();
    }

    @Test
    void testExportToOutputStream() throws Exception {
        List<TestUser> users = createTestUsers();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        excelExportUtil.export(out, "test", "用户列表", users, TestUser.class);

        byte[] result = out.toByteArray();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testExportEmptyList() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        excelExportUtil.export(out, "test", "空列表", new ArrayList<>(), TestUser.class);

        byte[] result = out.toByteArray();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testExportNullList() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        excelExportUtil.export(out, "test", "空列表", null, TestUser.class);

        byte[] result = out.toByteArray();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testExportNoExcelColumnAnnotation() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class, () ->
                excelExportUtil.export(out, "test", "测试", new ArrayList<>(), EmptyEntity.class)
        );
    }

    @Test
    void testBatchExport() throws Exception {
        List<List<TestUser>> batchList = new ArrayList<>();
        batchList.add(createTestUsers());
        batchList.add(createTestUsers());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportUtil.exportBatch(out, "test", "批量导出", batchList, TestUser.class);

        byte[] result = out.toByteArray();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testExportWithNullFields() throws Exception {
        TestUser user = new TestUser();
        user.setId(1L);
        user.setUsername(null);
        user.setAge(null);
        user.setCreateTime(null);
        user.setEnabled(null);
        user.setRemark(null);

        List<TestUser> users = new ArrayList<>();
        users.add(user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelExportUtil.export(out, "test", "空字段", users, TestUser.class);

        byte[] result = out.toByteArray();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    private List<TestUser> createTestUsers() {
        List<TestUser> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TestUser user = new TestUser();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setAge(20 + i);
            user.setCreateTime(new Date());
            user.setEnabled(i % 2 == 0);
            user.setRemark(i % 3 == 0 ? "VIP用户" : null);
            users.add(user);
        }
        return users;
    }
}
