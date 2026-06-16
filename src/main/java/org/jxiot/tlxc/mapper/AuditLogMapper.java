package org.jxiot.tlxc.mapper;

import org.jxiot.tlxc.entity.AuditLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AuditLogMapper {

    @Select("SELECT * FROM audit_log ORDER BY created_at DESC LIMIT #{limit}")
    List<AuditLog> findRecent(@Param("limit") int limit);

    @Insert("INSERT INTO audit_log(admin_id, action, target_type, target_id, detail, created_at) " +
            "VALUES(#{adminId}, #{action}, #{targetType}, #{targetId}, #{detail}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AuditLog auditLog);

    @Delete("DELETE FROM audit_log WHERE admin_id = #{adminId}")
    int deleteByAdminId(Integer adminId);
}
