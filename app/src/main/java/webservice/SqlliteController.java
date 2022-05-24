package webservice;

/**
 * Created by Firstline Infotech on 03-05-2019.
 */

    import java.sql.Date;
    import java.util.ArrayList;
    import java.util.HashMap;
    import android.util.Log;
    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

public class SqlliteController  extends SQLiteOpenHelper {
    private static final String LOGCAT = null;

    public SqlliteController(Context applicationcontext){
        super(applicationcontext, "androidsqlitestudentinformation.db", null, 1);
        Log.d(LOGCAT,"Created");
    }

    @Override
    public void onCreate(SQLiteDatabase database){
//        String query;
//        query = "DROP TABLE IF EXISTS templateshifttimings";
//        database.execSQL(query);
//        query= "CREATE TABLE IF NOT EXISTS templateshifttimings (templateid INTEGER PRIMARY KEY, templateshifttime TEXT)";
//        database.execSQL(query);
//
//        query = "DROP TABLE IF EXISTS templateclasstimetable";
//        database.execSQL(query);
//        query= "CREATE TABLE IF NOT EXISTS templateclasstimetable (templateid INTEGER,programsectionid INTEGER,dayorderdesc VARCHAR(20),hourid VARCHAR(30),shifttime varchar(40),subjects TEXT)";
//        database.execSQL(query);
//        Log.d(LOGCAT,"templateshifttimings Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
//        String query;
//        query = "DROP TABLE IF EXISTS Students";
//        database.execSQL(query);
//        onCreate(database);
    }

//    public void insertTemplateTimings(long lngTemplateId,String strShiftTimings){
//        String query;
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("templateid",lngTemplateId);
//        values.put("templateshifttime",strShiftTimings);
//        database.insert("templateshifttimings", null, values);
//        database.close();
//    }

    public void deleteLoginStudentDetails(){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM studentlogindetails";
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertLoginStudentDetails(long lngStudentId,String strStudentName,
                                          String strRegisterNo,String strProgram,
                                          String strSemester,String strSchool,int intCourseId){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS studentlogindetails (studentid INTEGER," +
                "studentname VARCHAR(75)," +
                "registerno VARCHAR(30)," +
                "program VARCHAR(100)," +
                "semester VARCHAR(10)," +
                "school VARCHAR(200)," +
                "courseid INTEGER," +
                "lastloggedin DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("studentname",strStudentName);
        values.put("registerno",strRegisterNo);
        values.put("program",strProgram);
        values.put("semester",strSemester);
        values.put("school",strSchool);
        values.put("courseid",intCourseId);
        database.insert("studentlogindetails", null, values);
        database.close();
    }

    public void insertProfileDetails(long lngStudentId,
                 String strStudentName,String strRegisterNo,
                 String strBatch,String strDob,String strGender,
                 String strSemester,String strSection,
                 String strFather,String strMother,String strAddress,
                 String strProgram, String strUniversityName,
                 String strAdmittedDate,String strSchool){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS profiledetails (studentid INTEGER PRIMARY KEY, " +
                "studentname VARCHAR(100)," +
                "registerno VARCHAR(30)," +
                "batch VARCHAR(30)," +
                "dob VARCHAR(20)," +
                "gender VARCHAR(10)," +
                "fathername VARCHAR(50)," +
                "mothername VARCHAR(50)," +
                "address TEXT," +
                "semester VARCHAR(50)," +
                "section VARCHAR(50)," +
                "program VARCHAR(100)," +
                "universityname VARCHAR(500)," +
                "admitteddate VARCHAR(20)," +
                "school VARCHAR(200)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        try {
            String deleteQuery = "DELETE FROM profiledetails where studentid=" + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch (Exception e){}
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("studentname",strStudentName);
        values.put("registerno",strRegisterNo);
        values.put("batch",strBatch);
        values.put("dob",strDob);
        values.put("gender",strGender);
        values.put("fathername",strFather);
        values.put("mothername",strMother);
        values.put("address",strAddress);
        values.put("semester",strSemester);
        values.put("section",strSection);
        values.put("program",strProgram);
        values.put("universityname",strUniversityName);
        values.put("admitteddate",strAdmittedDate);
        values.put("school",strSchool);
        database.insert("profiledetails", null, values);
        database.close();
    }

    public void insertStudentPhoto(long lngStudentId,byte[] photo){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS studentphoto (studentid INTEGER PRIMARY KEY, " +
                "studentphoto blob,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        try {
            String deleteQuery = "DELETE FROM studentphoto where studentid=" + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch (Exception e){}
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("studentphoto",photo);
        database.insert("studentphoto", null, values);
        database.close();
    }

//    public void insertFeePaid(long lngStudentId,
//                             String strFeeHead,
//                             String strReceiptDate,
//                             String strAmountPaid){
//        SQLiteDatabase database = this.getWritableDatabase();
//        String query= "CREATE TABLE IF NOT EXISTS studentfeepaid (studentid INTEGER, " +
//                "feehead VARCHAR(75)," +
//                "receiptdate VARCHAR(30)," +
//                "amountpaid REAL," +
//                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
//        database.execSQL(query);
//        ContentValues values = new ContentValues();
//        values.put("studentid",lngStudentId);
//        values.put("feehead",strFeeHead);
//        values.put("receiptdate",strReceiptDate);
//        values.put("amountpaid",Double.parseDouble(strAmountPaid));
//        database.insert("studentfeepaid", null, values);
//        database.close();
//    }
//

    public void deleteFeeDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM feedetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertFeeDetails(long lngStudentId,String strDueName,String strDueamount,String strCurrentDue,String strDueDate,String strTerm,String strAmtCollected){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS feedetails (studentid INTEGER, " +
                "duename VARCHAR(75)," +
                "dueamount REAL," +
                "currentdue REAL," +
                "duedate VARCHAR(30)," +
                "term VARCHAR(30)," +
                "amountcollected REAL," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("duename",strDueName);
        values.put("dueamount",strDueamount);
        values.put("currentdue",strCurrentDue);
        values.put("duedate",strDueDate);
        values.put("term",strTerm);
        values.put("amountcollected",strAmtCollected);
        Log.d("insert query",String.valueOf(values));
        database.insert("feedetails", null, values);
        database.close();
    }

    public void deleteFinanceDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM financedetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertFinanceDetails(long lngStudentId,String strTerm,String strDueName,String strDueamount,String strDueDate,String strReceiptDate,
                                     String strAmtCollected,String strMode,String strReceiptNum){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS financedetails (studentid INTEGER, " +
                "term VARCHAR(20)," +
                "duename VARCHAR(75)," +
                "dueamount REAL," +
                "duedate VARCHAR(30)," +
                "receiptdate VARCHAR(30)," +
                "amountcollected REAL," +
                "mode VARCHAR(50)," +
                "receiptnum VARCHAR(30)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("term",strTerm);
        values.put("duename",strDueName);
        values.put("dueamount",strDueamount);
        values.put("duedate",strDueDate);
        values.put("receiptdate",strReceiptDate);
        values.put("amountcollected",strAmtCollected);
        values.put("mode",strMode);
        values.put("receiptnum",strReceiptNum);

        Log.d("insert query",String.valueOf(values));
        database.insert("financedetails", null, values);
        database.close();
    }

    public void deleteNotificationDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM notificationdetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            String query= "DROP TABLE notificationdetails ";
            database.execSQL(query);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertNotificationDetails(long lngStudentId,String strNotificationDate,String strNotificationTime,String strNotificationTitle,
                                          String strNotificationMessage,String strSendingEmployee,
                                          String strDepartment,String strDesignation){
        SQLiteDatabase database = this.getWritableDatabase();
        //String query= "DROP TABLE notificationdetails ";
        //database.execSQL(query);
        String query= "CREATE TABLE IF NOT EXISTS notificationdetails (studentid INTEGER, " +
                "notificationtitle VARCHAR(50)," +
                "notificationmessage TEXT," +
                "notificationdate DATE," +
                "notificationtime TIME," +
                "sendingemployee VARCHAR(75)," +
                "department VARCHAR(30)," +
                "designation VARCHAR(100)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("notificationtitle",strNotificationTitle);
        values.put("notificationmessage",strNotificationMessage);
        values.put("notificationdate",strNotificationDate);
        values.put("notificationtime",strNotificationTime);
        values.put("sendingemployee",strSendingEmployee);
        values.put("department",strDepartment);
        values.put("designation",strDesignation);
        Log.d("insert query",String.valueOf(values));
        database.insert("notificationdetails", null, values);
        database.close();
    }

    public void deleteHostelDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM hosteldetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertHostelDetails(long lngStudentId,String strAcaYear,String strAllotedDate,String strHostelName,String strRoomName,String strRoomType){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS hosteldetails (studentid INTEGER, " +
                "academicyear VARCHAR(20)," +
                "alloteddate VARCHAR(30)," +
                "hostelname VARCHAR(150)," +
                "roomname VARCHAR(100)," +
                "roomtype VARCHAR(75)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("academicyear",strAcaYear);
        values.put("alloteddate",strAllotedDate);
        values.put("hostelname",strHostelName);
        values.put("roomname",strRoomName);
        values.put("roomtype",strRoomType);
        Log.d("insert query",String.valueOf(values));
        database.insert("hosteldetails", null, values);
        database.close();
    }

    public void deleteStudentSubjects(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM studentsubjects where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertStudentSubjects(long lngStudentId,String strSemester,String strSubCode,String strSubjectDesc,String strCredit){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS studentsubjects (studentid INTEGER, " +
                "semester VARCHAR(20)," +
                "subjectcode VARCHAR(10)," +
                "subjectdesc VARCHAR(75)," +
                "credit VARCHAR(20)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("semester",strSemester);
        values.put("subjectcode",strSubCode);
        values.put("subjectdesc",strSubjectDesc);
        values.put("credit",strCredit);
        Log.d("insert query",String.valueOf(values));
        database.insert("studentsubjects", null, values);
        database.close();
    }

    public void deleteSubjectAttendance(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM subjectattendance where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertSubjectAttendance(long lngStudentId,String strSubjectCode,String strSubjectDesc,String strPresent,String strAbsent,String strTotal,String strPresentPercentage){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS subjectattendance (studentid INTEGER, " +
                "subjectcode VARCHAR(10)," +
                "subjectdesc VARCHAR(75)," +
                "presenthrs REAL,"+
                "absenthrs REAL,"+
                "totalhrs REAL,"+
                "presentpercentage REAL,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("subjectcode",strSubjectCode);
        values.put("subjectdesc",strSubjectDesc);
        values.put("presenthrs",strPresent);
        values.put("absenthrs",strAbsent);
        values.put("totalhrs",strTotal);
        values.put("presentpercentage",strPresentPercentage);
        Log.d("insert query",String.valueOf(values));
        database.insert("subjectattendance", null, values);
        database.close();
    }

    public void deleteCummulativeAttendance(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM cummulativeattendance where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertCummulativeAttendance(long lngStudentId,String strAttendancemonthyear,String strPresent,String strAbsent,String strODPresent,String strODAbsent,
                                            String strMedical){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS cummulativeattendance (studentid INTEGER, " +
                "attendancemonthyear VARCHAR(10)," +
                "presenthrs REAL,"+
                "absenthrs REAL,"+
                "odpresent REAL,"+
                "odabsent REAL,"+
                "medical REAL,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("attendancemonthyear",strAttendancemonthyear);
        values.put("presenthrs",strPresent);
        values.put("absenthrs",strAbsent);
        values.put("odpresent",strODPresent);
        values.put("odabsent",strODAbsent);
        values.put("medical",strMedical);
        Log.d("insert query",String.valueOf(values));
        database.insert("cummulativeattendance", null, values);
        database.close();
    }

    public void deleteHourwiseAttendance(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM hourwiseattendance  where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
//            deleteQuery = "DROP TABLE hourwiseattendance ";
//            Log.d("query", deleteQuery);
//            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertHourwiseAttendance(long lngStudentId,String strAttendancedate,String strh1,String strh2,String strh3,String strh4,
                                            String strh5, String strh6, String strh7,String strh8){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS hourwiseattendance (studentid INTEGER, " +
                "attendancedate VARCHAR(30)," +
                "h1 INTEGER,"+
                "h2 INTEGER,"+
                "h3 INTEGER,"+
                "h4 INTEGER,"+
                "h5 INTEGER,"+
                "h6 INTEGER,"+
                "h7 INTEGER,"+
                "h8 INTEGER,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("attendancedate",strAttendancedate);
        values.put("h1",strh1);
        values.put("h2",strh2);
        values.put("h3",strh3);
        values.put("h4",strh4);
        values.put("h5",strh5);
        values.put("h6",strh6);
        values.put("h7",strh7);
        values.put("h8",strh8);
        Log.d("insert query",String.valueOf(values));
        database.insert("hourwiseattendance", null, values);
        database.close();
    }

    public void deleteInternalMarkDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM internalmarkdetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertInternalMarkDetails(long lngStudentId,String strSubCode,String strSubjectDesc,String strMarkObtained,String strMaxMarks){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS internalmarkdetails (studentid INTEGER, " +
                "subjectcode VARCHAR(10)," +
                "subjectdesc VARCHAR(75)," +
                "markobtained REAL," +
                "maxmarks REAL," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("subjectcode",strSubCode);
        values.put("subjectdesc",strSubjectDesc);
        values.put("markobtained",strMarkObtained);
        values.put("maxmarks",strMaxMarks);
        Log.d("insert query",String.valueOf(values));
        database.insert("internalmarkdetails", null, values);
        database.close();
    }

    public void deleteExamDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM examdetails where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertExamDetails(long lngStudentId,String strSemester,String strMonthYear,
                          String strSubCode,String strSubjectDesc,String strMarkObtained,String strInternal,
                                  String strExternal,String strCredit,String strGrade,String strResult,String strattempts){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS examdetails (studentid INTEGER, " +
                "semester VARCHAR(5)," +
                "monthyear VARCHAR(20)," +
                "subjectcode VARCHAR(10)," +
                "subjectdesc VARCHAR(75)," +
                "markobtained REAL," +
                "internal REAL," +
                "external REAL," +
                "credit INTEGER," +
                "grade VARCHAR(3)," +
                "result VARCHAR(15)," +
                "attempts INTEGER,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("semester",strSemester);
        values.put("monthyear",strMonthYear);
        values.put("subjectcode",strSubCode);
        values.put("subjectdesc",strSubjectDesc);
        values.put("markobtained",strMarkObtained);
        values.put("internal",strInternal);
        values.put("external",strExternal);
        values.put("credit",strCredit);
        values.put("grade",strGrade);
        values.put("result",strResult);
        values.put("attempts",strattempts);
        Log.d("insert query",String.valueOf(values));
        database.insert("examdetails", null, values);
        database.close();
    }

    public void deleteLibraryTransaction(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM librarytransaction  where studentid = " + lngStudentId;
            Log.d("query", deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertLibraryTransaction(long lngStudentId,String strIssuedate,String strAccessionNo,String strTitle,String strDueDate,String strReturnDate,
                                         String strStatus,String strFine){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS librarytransaction (studentid INTEGER, " +
                "issuedate VARCHAR(30)," +
                "accessionno VARCHAR(15),"+
                "title VARCHAR(150),"+
                "duedate VARCHAR(30)," +
                "returndate VARCHAR(30)," +
                "status VARCHAR(20),"+
                "fine REAL,"+
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        ContentValues values = new ContentValues();
        values.put("studentid",lngStudentId);
        values.put("issuedate",strIssuedate);
        values.put("accessionno",strAccessionNo);
        values.put("title",strTitle);
        values.put("duedate",strDueDate);
        values.put("returndate",strReturnDate);
        values.put("status",strStatus);
        values.put("fine",strFine);
        Log.d("insert query",String.valueOf(values));
        database.insert("librarytransaction", null, values);
        database.close();
    }

    public void deleteMemberDetails(long lngStudentId){
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        try{
            String deleteQuery = "DELETE FROM memberdetails where studentid = " + lngStudentId;
            Log.d("query",deleteQuery);
            database.execSQL(deleteQuery);
        }catch(Exception e){

        }
    }

    public void insertMemberDetails(long lngStudentId,String strMemberCode,String strMemberType,String strMemberName,String strPolicyName,String strStatus){
        SQLiteDatabase database = this.getWritableDatabase();
        String query= "CREATE TABLE IF NOT EXISTS memberdetails (studentid INTEGER, " +
                "membercode VARCHAR(15)," +
                "membertype VARCHAR(100)," +
                "membername VARCHAR(125)," +
                "policyname VARCHAR(100)," +
                "status VARCHAR(10)," +
                "lastupdatedate DATETIME DEFAULT (datetime('now','localtime')))";
        database.execSQL(query);
        try {
            ContentValues values = new ContentValues();
            values.put("studentid", lngStudentId);
            values.put("membercode", strMemberCode);
            values.put("membertype", strMemberType);
            values.put("membername", strMemberName);
            values.put("policyname", strPolicyName);
            values.put("status", strStatus);
            Log.d("insert query", String.valueOf(values));
            database.insert("memberdetails", null, values);
            database.close();
        }catch (Exception e){
            Log.e("Problem", e + " ");
        }
    }
//
//    public void deleteSubjectContent(long lngStudentId){
//        Log.d(LOGCAT,"delete");
//        SQLiteDatabase database = this.getWritableDatabase();
//        try{
//            String deleteQuery = "DELETE FROM subjectcontent where studentid = " + lngStudentId;
//            Log.d("query",deleteQuery);
//            database.execSQL(deleteQuery);
//        }catch(Exception e){
//
//        }
//    }
//
//    public void deleteBroadcast(long lngStudentId) {
//        Log.d(LOGCAT,"delete");
//        SQLiteDatabase database = this.getWritableDatabase();
//        try{
//            String deleteQuery = "DELETE FROM broadcast where studentid = " + lngStudentId;
//            Log.d("query",deleteQuery);
//            database.execSQL(deleteQuery);
//        }catch(Exception e){
//
//        }
//    }
//
//    public void deleteFeePaid(long lngStudentId) {
//        Log.d(LOGCAT,"delete");
//        SQLiteDatabase database = this.getWritableDatabase();
//        try{
//            String deleteQuery = "DELETE FROM studentfeepaid where studentid = " + lngStudentId;
//            Log.d("query",deleteQuery);
//            database.execSQL(deleteQuery);
//        }catch(Exception e){
//
//        }
//    }
//
//    public void insertTemplateClassTimeTable(long lngTemplateId,long lngProgSecId,String strDayOrderDesc,String strHourId,String strShiftTime,String strSubjects){
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("templateid",lngTemplateId);
//        values.put("programsectionid",lngProgSecId);
//        values.put("dayorderdesc",strDayOrderDesc);
//        values.put("hourid",strHourId);
//        values.put("shifttime",strShiftTime);
//        values.put("subjects",strSubjects);
//        database.insert("templateclasstimetable", null, values);
//        database.close();
//    }
//
//    public void insertStudentFeePaid(long lngStudentId,String strDueDetails,String strReceiptDetails){
//        SQLiteDatabase database = this.getWritableDatabase();
//        String query= "CREATE TABLE IF NOT EXISTS studentfeepaid (studentid INTEGER,duedetails TEXT,receiptdetails TEXT)";
//        database.execSQL(query);
//
//        ContentValues values = new ContentValues();
//        values.put("studentid",lngStudentId);
//        values.put("duedetails",strDueDetails);
//        values.put("receiptdetails",strReceiptDetails);
//        database.insert("studentfeepaid", null, values);
//        database.close();
//    }

//    public void insertStudent(HashMap<String, String> queryValues) {
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("StudentName", queryValues.get("StudentName"));
//        database.insert("Students", null, values);
//        database.close();
//    }
//
//    public int updateStudent(HashMap<String, String> queryValues) {
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("StudentName", queryValues.get("StudentName"));
//        return database.update("Students", values, "StudentId" + " = ?", new String[] { queryValues.get("StudentId") });
//        //String updateQuery = "Update  words set txtWord='"+word+"' where txtWord='"+ oldWord +"'";
//        //Log.d(LOGCAT,updateQuery);
//        //database.rawQuery(updateQuery, null);
//        //return database.update("words", values, "txtWord  = ?", new String[] { word });
//    }
//
//    public void deleteShiftTime(long id){
//        Log.d(LOGCAT,"delete");
//        SQLiteDatabase database = this.getWritableDatabase();
//        try{
//            String deleteQuery = "DELETE FROM templateshifttimings where templateid=" + id;
//            Log.d("query",deleteQuery);
//            database.execSQL(deleteQuery);
//        }catch(Exception e){
//
//        }
//    }
//
//    public void deleteClassTimeTable(long templateid,long secid) {
//        Log.d(LOGCAT,"delete");
//        SQLiteDatabase database = this.getWritableDatabase();
//        try{
//            String deleteQuery = "DELETE FROM templateclasstimetable where templateid="+ templateid + " AND programsectionid=" + secid;
//            Log.d("query",deleteQuery);
//            database.execSQL(deleteQuery);
//        }catch(Exception e){
//
//        }
//    }

//    public ArrayList<HashMap<String, String>> getTimeTable(long lngTemplateId) {
//        ArrayList<HashMap<String, String>> shifttimingList;
//        shifttimingList = new ArrayList<HashMap<String, String>>();
//        String selectQuery = "SELECT dayorderdesc,subjects FROM templateclasstimetable WHWERE templateid="+lngTemplateId;
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor cursor = database.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()){
//            do {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("DayOrder", cursor.getString(0));
//                map.put("ShiftTime", cursor.getString(1));
//                shifttimingList.add(map);
//            } while (cursor.moveToNext());
//        }
//        return shifttimingList;
//    }

//    public HashMap<String, String> getStudentInfo(String id) {
//        HashMap<String, String> wordList = new HashMap<String, String>();
//        SQLiteDatabase database = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM Students where StudentId='"+id+"'";
//        Cursor cursor = database.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                //HashMap<String, String> map = new HashMap<String, String>();
//                wordList.put("StudentName", cursor.getString(1));
//                //wordList.add(map);
//            } while (cursor.moveToNext());
//        }
//        return wordList;
//    }
}



