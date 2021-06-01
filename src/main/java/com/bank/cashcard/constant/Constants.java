package com.bank.cashcard.constant;

public class Constants {

	public static final String DEPOSIT="deposit";
	public static final String WITHDRAW="withdraw";
	public static final String BALANCE="balance";
	public static final String TRUE="true";
	
	public static final double MAX_DEPOSIT_PER_TRANSACTION = 40000;
	public static final double MAX_DEPOSIT_PER_DAY = 150000; 
	public static final int MAX_DEPOSIT_TRANSACTIONS_PER_DAY = 5;
	
	public static final double MAX_WITHDRAWAL_PER_TRANSACTION = 20000; 
	public static final double MAX_WITHDRAWAL_PER_DAY = 50000; 
	public static final int MAX_WITHDRAWAL_TRANSACTIONS_PER_DAY = 3;
/***********************************************Jpa Properties ******************************************/
	
	public static final String HIBERNATE_DIALECT="hibernate.dialect";
	public static final String HIBERNATE_HBM2DDL_AUTO="hibernate.hbm2ddl.auto";
	public static final String HIBERNATE_SHOW_SQL="hibernate.show_sql";
	public static final String HIBERNATE_FORMAT_SQL="hibernate.format_sql";
	public static final String HIBERNATE_SQL_COMMENTS="hibernate.use_sql_comments";
	public static final String HIBERNATE_LAZY_LOAD="hibernate.enable_lazy_load_no_trans";
	
	public static final String CACHE_PREP_STMTS="cachePrepStmts";
	public static final String PREPSTMT_CACHE_SIZE="prepStmtCacheSize";
	public static final String PREPSTMT_CACHE_SQL_LIMIT="prepStmtCacheSqlLimit";
	public static final String USE_SERVER_PREPSTMT="useServerPrepStmts";
	
	public static final String PREPSTMT_CACHE_SIZE_NO="250";
	public static final String PREPSTMT_CACHE_SQL_LIMIT_NO="2048";
}
