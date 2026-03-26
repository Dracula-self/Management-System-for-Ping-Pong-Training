package com.quan.project.entity;

/**
 * 球台实体类
 * 对应数据库表：tables
 */
public class Table {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 所属校区ID
     */
    private Integer campusId;
    
    /**
     * 球台编号
     */
    private String tableNumber;
    
    // 无参构造方法
    public Table() {}
    
    // 全参构造方法
    public Table(Integer id, Integer campusId, String tableNumber) {
        this.id = id;
        this.campusId = campusId;
        this.tableNumber = tableNumber;
    }
    
    // 便捷构造方法
    public Table(Integer campusId, String tableNumber) {
        this.campusId = campusId;
        this.tableNumber = tableNumber;
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCampusId() {
        return campusId;
    }
    
    public void setCampusId(Integer campusId) {
        this.campusId = campusId;
    }
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", campusId=" + campusId +
                ", tableNumber='" + tableNumber + '\'' +
                '}';
    }
}
