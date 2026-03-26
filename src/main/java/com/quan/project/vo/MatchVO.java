package com.quan.project.vo;

import com.quan.project.entity.Match;

/**
 * 比赛对阵VO，用于返回包含选手姓名等详细信息的对阵数据
 */
public class MatchVO extends Match {
    
    /**
     * 选手1姓名
     */
    private String player1Name;
    
    /**
     * 选手2姓名
     */
    private String player2Name;
    
    /**
     * 获胜者姓名
     */
    private String winnerName;
    
    /**
     * 比赛球台名称
     */
    private String tableName;
    
    /**
     * 比赛状态：0-未开始，1-进行中，2-已完成
     */
    private Integer matchStatus;
    
    public MatchVO() {
        super();
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
    
    public String getWinnerName() {
        return winnerName;
    }
    
    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public Integer getMatchStatus() {
        return matchStatus;
    }
    
    public void setMatchStatus(Integer matchStatus) {
        this.matchStatus = matchStatus;
    }
    
    /**
     * 获取比赛状态文本
     */
    public String getMatchStatusText() {
        if (this.matchStatus == null) {
            return "未知";
        }
        switch (this.matchStatus) {
            case 0:
                return "未开始";
            case 1:
                return "进行中";
            case 2:
                return "已完成";
            default:
                return "未知";
        }
    }
    
    @Override
    public String toString() {
        return "MatchVO{" +
                "id=" + getId() +
                ", competitionId=" + getCompetitionId() +
                ", groupLevel=" + getGroupLevel() +
                ", roundNumber=" + getRoundNumber() +
                ", player1Name='" + player1Name + '\'' +
                ", player2Name='" + player2Name + '\'' +
                ", winnerName='" + winnerName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", matchStatus=" + matchStatus +
                ", score='" + getScore() + '\'' +
                '}';
    }
}
