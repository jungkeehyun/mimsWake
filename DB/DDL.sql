CREATE TABLE mimsWake.clientConn (
	CLIENT_CONN_ID		VARCHAR(15)	not null	-- [PK] ID
	, CONN_DTTM		VARCHAR(14)	not null	-- 접속일시
	, CLIENT_CONN_IP	VARCHAR(10)	not null	-- 접속IP
	, MODE			VARCHAR(5)	not null	-- 접속모드(Group ID)
	, MSG_TYPE		VARCHAR(5)	not null	-- 메시지종류 (Client ID)
	, REGR_DTTM		VARCHAR(14)	not null	-- [BASE] 등록일시
)

CREATE TABLE mimsWake.airWake (
	AIR_WAKE_ID		VARCHAR (15)	not null	-- [PK] ID 
	, ACFTNO		VARCHAR(10)	not null	-- 항공기번호
	, ACFTFNFDVCD		VARCHAR(2)	not null	-- 항공기피아구분코드
	, ALT			FLOAT(8,2)	not null	-- 고도 (F8.2)
	, ACFT_CNTUNT		INT				-- 항공기대수 (I7)
	, CLSGN			VARCHAR(50)	not null	-- 호출부호
	, TKOF_BASENO		VARCHAR(5)	not null	-- 이륙기지번호
	, SPD			FLOAT(13,3)	not null	-- 속력 (F13.3)
	, KNDAP_NM 		VARCHAR(50)	not null	-- 항공기종명
	, LTDLNGT_COORD		VARCHAR(50)	not null	-- 위경도좌표
	, AZ			INT		not null	-- 방위각 (I4)
	, REGR_DTTM		VARCHAR(14)	not null	-- [BASE] 등록일시
);

CREATE TABLE mimsWake.seaWakeRe (
	SEA_WAKE_RE_ID		VARCHAR(15)	not null	-- ID (PK)
	, SEATIFFCD		VARCHAR(1)	not null	-- 해상항적피아식별코드
	, IDNO			VARCHAR(15)			-- 식별번호
	, WRSHPNO		VARCHAR(10)	not null	-- 함정번호
	, LOCTN			VARCHAR(50)			-- 위치
	, AZ			INT				-- 방위각 (I4)
	, SEATTGTMSCD		VARCHAR(2)			-- 해상항적표적임무코드
	, SEATNATCD		VARCHAR(2)			-- 해상항적국가코드
	, SPD			FLOAT(13,3)			-- 속력 (F13.3)
	, SEATTGTLRCCD		VARCHAR(2)			-- 해상항적표적하위범주코드
	, SEATTGTKNDCD		VARCHAR(3)			-- 해상항적표적종류코드
	, EFTV_DTTM		VARCHAR(14)			-- 유효일시 (DT)
	, TRACRLTDCD		VARCHAR(1)			-- 항적실시간구분코드
	, REGR_DTTM		VARCHAR(14)	not null	-- [BASE] 등록일시
}

CREATE TABLE mimsWake.seaWakeEx (
	SEA_WAKE_EX_ID		VARCHAR(15)	not null	-- ID (PK)
	, SEATIFFCD		VARCHAR(1)	not null	-- 해상항적피아식별코드
	, IDNO			VARCHAR(15)			-- 식별번호
	, WRSHPNO		VARCHAR(10)	not null	-- 함정번호
	, LOCTN			VARCHAR(50)			-- 위치
	, AZ			INT				-- 방위각 (I4)
	, SPD			FLOAT(13,3)			-- 속력 (F13.3)
	, WARSBLCD		VARCHAR(15)			-- 군대부호코드
	, REGR_DTTM		VARCHAR(14)	not null	-- [BASE] 등록일시
)
