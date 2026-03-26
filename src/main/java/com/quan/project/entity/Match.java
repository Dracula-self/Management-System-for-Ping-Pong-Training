package com.quan.project.entity;

/**
 * 比赛对阵实体类
 * 对应数据库表：matches
 */
public class Match {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 比赛ID
     */
    private Integer competitionId;
    
    /**
     * 组别: 1-甲, 2-乙, 3-丙
     */
    private Integer groupLevel;
    
    /**
     * 轮次
     */
    private Integer roundNumber;
    
    /**
     * 选手1 ID
     */
    private Integer player1Id;
    
    /**
     * 选手2 ID (轮空时为空)
     */
    private Integer player2Id;
    
    /**
     * 比赛球台
     */
    private Integer tableId;
    
    /**
     * 获胜者ID
     */
    private Integer winnerId;
    
    /**
     * 比分
     */
    private String score;
    
    // 无参构造方法
    public Match() {}
    
    // 全参构造方法
    public Match(Integer id, Integer competitionId, Integer groupLevel, Integer roundNumber,
                Integer player1Id, Integer player2Id, Integer tableId, Integer winnerId, String score) {
        this.id = id;
        this.competitionId = competitionId;
        this.groupLevel = groupLevel;
        this.roundNumber = roundNumber;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.tableId = tableId;
        this.winnerId = winnerId;
        this.score = score;
    }
    
    // 便捷构造方法（创建对阵）
    public Match(Integer competitionId, Integer groupLevel, Integer roundNumber,
                Integer player1Id, Integer player2Id, Integer tableId) {
        this.competitionId = competitionId;
        this.groupLevel = groupLevel;
        this.roundNumber = roundNumber;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.tableId = tableId;
    }
    
    /**
     * 获取组别文本描述
     */
    public String getGroupLevelText() {
        switch (this.groupLevel) {
            case 1:
                return "甲组";
            case 2:
                return "乙组";
            case 3:
                return "丙组";
            default:
                return "未知组别";
        }
    }
    
    /**
     * 判断是否为轮空比赛
     */
    public boolean isByeMatch() {
        return this.player2Id == null;
    }
    
    /**
     * 判断比赛是否已完成
     */
    public boolean isCompleted() {
        return this.winnerId != null && this.score != null && !this.score.trim().isEmpty();
    }
    
    // Getter和Setter方法
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCompetitionId() {
        return competitionId;
    }
    
    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }
    
    public Integer getGroupLevel() {
        return groupLevel;
    }
    
    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }
    
    public Integer getRoundNumber() {
        return roundNumber;
    }
    
    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }
    
    public Integer getPlayer1Id() {
        return player1Id;
    }
    
    public void setPlayer1Id(Integer player1Id) {
        this.player1Id = player1Id;
    }
    
    public Integer getPlayer2Id() {
        return player2Id;
    }
    
    public void setPlayer2Id(Integer player2Id) {
        this.player2Id = player2Id;
    }
    
    public Integer getTableId() {
        return tableId;
    }
    
    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    
    public Integer getWinnerId() {
        return winnerId;
    }
    
    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }
    
    public String getScore() {
        return score;
    }
    
    public void setScore(String score) {
        this.score = score;
    }
    
    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", competitionId=" + competitionId +
                ", groupLevel=" + groupLevel +
                ", roundNumber=" + roundNumber +
                ", player1Id=" + player1Id +
                ", player2Id=" + player2Id +
                ", tableId=" + tableId +
                ", winnerId=" + winnerId +
                ", score='" + score + '\'' +
                '}';
    }
}
