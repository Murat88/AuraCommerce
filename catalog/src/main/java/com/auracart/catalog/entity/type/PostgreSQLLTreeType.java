package com.auracart.catalog.entity.type;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * PostgreSQL {@code ltree} kolonunu düz Java {@link String} olarak map eden hafif bir
 * Hibernate {@link UserType} implementasyonu.
 * <p>
 * NOT: Bu proje Hibernate 7.4 kullanıyor; Hypersistence Utils kütüphanesinin (bkz.
 * {@code io.hypersistence:hypersistence-utils-hibernate-63}) şu an için bir Hibernate 7
 * sürümü bulunmuyor ve mevcut en güncel sürümü {@code org.hibernate.query.BindableType}
 * sınıfının kaldırılmış olması nedeniyle uygulama başlangıcında
 * {@code NoClassDefFoundError} fırlatıyor. Bu sınıf, o kütüphanenin
 * {@code PostgreSQLLTreeType}'ının yaptığı işi (değeri {@link PGobject} ile
 * {@code ltree} tipi olarak JDBC'ye bağlamak) minimal biçimde yeniden uygular; böylece
 * hem uygulama sorunsuz başlar hem de {@code path} kolonuna yazma/okuma doğru şekilde
 * çalışır (tip uyuşmazlığı hatası olmadan).
 */
public class PostgreSQLLTreeType implements UserType<String> {

    private static final String PG_TYPE_NAME = "ltree";

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(String x, String y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(String x) {
        return Objects.hashCode(x);
    }

    @Override
    public String nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        Object value = rs.getObject(position);
        return value == null ? null : value.toString();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        PGobject pgObject = new PGobject();
        pgObject.setType(PG_TYPE_NAME);
        pgObject.setValue(value);
        st.setObject(index, pgObject);
    }

    @Override
    public String deepCopy(String value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String value) {
        return value;
    }

    @Override
    public String assemble(Serializable cached, Object owner) {
        return (String) cached;
    }
}

