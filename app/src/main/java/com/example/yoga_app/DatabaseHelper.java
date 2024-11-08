package com.example.yoga_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;
import com.example.yoga_app.model.Role;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Khai báo tên Database và các bảng
    public static final String DATABASE_NAME = "yoga_app.db";
    public static final String USER_TABLE = "users";
    public static final String ROLE_TABLE = "roles";
    public static final String COURSE_TABLE = "courses";
    public static final String CLASS_TABLE = "classes";

    public static final String USER_ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ROLE_ID = "role_id";

    public static final String ROLE_ID_COL = "id";
    public static final String ROLE_NAME = "role_name";

    public static final String COURSE_ID = "id";
    public static final String COURSE_NAME = "course_name";
    public static final String COURSE_TYPE = "course_type";
    public static final String COURSE_PRICE = "price";
    public static final String COURSE_DURATION = "duration";
    public static final String COURSE_CAPACITY = "capacity";
    public static final String COURSE_DESCRIPTION = "description";
    public static final String COURSE_DAY = "courseDay";
    public static final String COURSE_TIME = "courseDayTime";

    // Cột cho bảng phiên học (Class Instances)
    public static final String CLASS_ID = "id";
    public static final String CLASS_COURSE_ID = "course_id";
    public static final String CLASS_NAME = "name";
    public static final String CLASS_DATE = "date";
    public static final String CLASS_INSTRUCTOR = "instructor";
    public static final String CLASS_COMMENTS = "comments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 9);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tạo bảng vai trò (Roles)
        String CREATE_ROLE_TABLE = "CREATE TABLE " + ROLE_TABLE + " (" +
                ROLE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ROLE_NAME + " TEXT NOT NULL);";

        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT NOT NULL, " +
                EMAIL + " TEXT NOT NULL UNIQUE, " +
                PASSWORD + " TEXT NOT NULL, " +
                ROLE_ID + " INTEGER, " +
                "FOREIGN KEY (" + ROLE_ID + ") REFERENCES " + ROLE_TABLE + "(" + ROLE_ID_COL + "));";

        String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE + " (" +
                COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_DAY + " TEXT NOT NULL, " +
                COURSE_TIME + " TEXT NOT NULL, " +
                COURSE_NAME + " TEXT NOT NULL, " +
                COURSE_TYPE + " TEXT NOT NULL, " +
                COURSE_PRICE + " REAL NOT NULL, " +
                COURSE_DURATION + " INTEGER NOT NULL, " +
                COURSE_CAPACITY + " TEXT NOT NULL, " +
                COURSE_DESCRIPTION + " TEXT);";

        String CREATE_CLASS_TABLE = "CREATE TABLE " + CLASS_TABLE + " (" +
                CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLASS_COURSE_ID + " INTEGER, " +
                CLASS_NAME + " TEXT NOT NULL, " +
                CLASS_DATE + " TEXT NOT NULL, " +
                CLASS_INSTRUCTOR + " TEXT NOT NULL, " +
                CLASS_COMMENTS + " TEXT, " +
                "FOREIGN KEY (" + CLASS_COURSE_ID + ") REFERENCES " + COURSE_TABLE + "(" + COURSE_ID + "));";

        db.execSQL(CREATE_ROLE_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);

        insertDefaultRoles(db);
//        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CLASS_TABLE);
        onCreate(db);
    }


    // DEFAULT VALUES
    private void insertDefaultRoles(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(ROLE_NAME, "Admin");
        db.insert(ROLE_TABLE, null, values);

        values.put(ROLE_NAME, "User");
        db.insert(ROLE_TABLE, null, values);

        values.put(ROLE_NAME, "Instructor");
        db.insert(ROLE_TABLE, null, values);
    }

//    private void insertDefaultAdmin(SQLiteDatabase db){
//        ContentValues rawValue = new ContentValues();
//        rawValue.put(NAME, "admin");
//        rawValue.put(EMAIL, "admin@gmail.com");
//        rawValue.put(PASSWORD, "123456");
//        rawValue.put(ROLE_ID, 1);
//        db.insert(USER_TABLE, null, rawValue);
//    }
    //===========================================================//
    //=============== FUNCTIONS FOR ACCOUNT USERS ===============//
    //===========================================================//

    public boolean insertUser(String name, String email, String password, int role_id) { //For register in registration page
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(EMAIL, email);
        contentValues.put(PASSWORD, password);
        contentValues.put(ROLE_ID, role_id);
        long result = db.insert(USER_TABLE, null, contentValues);
        return result != -1;
    }

    public int getLastInsertedInstructorId() {
        int lastId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + USER_TABLE, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0);
            }
            cursor.close();
        }
        return lastId;
    }

    public boolean insertInstructorInDatabase(String name, String email, String password, int role_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(EMAIL, email);
        contentValues.put(PASSWORD, password);
        contentValues.put(ROLE_ID, role_id);
        long result = db.insert(USER_TABLE, null, contentValues);
        return result != -1;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
                int role_id = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE_ID));

                Instructor instructor = new Instructor(id, name, email, password, role_id); // Khởi tạo đối tượng Instructor
                instructors.add(instructor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instructors;
    }

    public boolean deleteUserByEmail(String email) { //delete account
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE, EMAIL + "=?", new String[]{email}) > 0;
    }


    public Instructor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Instructor instructor = null;

        // Query to select the user by ID
        Cursor cursor = db.query(USER_TABLE, null, USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Get info from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
            int roleId = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE_ID));

            instructor = new Instructor(userId, name, email, password, roleId);
        }
        if (cursor != null) {
            cursor.close();
        }
        return instructor;
    }

    public String getUserNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userName = null;

        Cursor cursor = db.query(USER_TABLE, new String[]{NAME}, EMAIL + "=?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
        }

        if (cursor != null) {
            cursor.close();
        }
        return userName;
    }

    public boolean updateInstructor(int instructorId, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(EMAIL, email);

        int result = db.update(USER_TABLE, contentValues, USER_ID + "=?", new String[]{String.valueOf(instructorId)});
        return result > 0;
    }

    public boolean deleteInstructor(int instructorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE, USER_ID + "=?", new String[]{String.valueOf(instructorId)}) > 0; // Return true if delete is successful
    }

    public boolean isInstructorExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + EMAIL + " = ?", new String[]{email});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public boolean addInstructor(Instructor instructor) {
        // Add new instructor into SQLite
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, instructor.getName());
        contentValues.put(EMAIL, instructor.getEmail());
        contentValues.put(PASSWORD, instructor.getPassword());
        contentValues.put(ROLE_ID, instructor.getRoleId());

        long result = db.insert(USER_TABLE, null, contentValues);
        db.close();

        return result != -1;
    }


    //===========================================================//
    //================== FUNCTIONS FOR ROLES ====================//
    //===========================================================//

    public Role getRoleById(int roleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Role role = null;

        Cursor cursor = db.query(ROLE_TABLE, new String[]{ROLE_ID_COL, ROLE_NAME}, ROLE_ID_COL + "=?", new String[]{String.valueOf(roleId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE_ID_COL));
            String roleName = cursor.getString(cursor.getColumnIndexOrThrow(ROLE_NAME));
            role = new Role(id, roleName);
        }
        if (cursor != null) {
            cursor.close();
        }
        return role;
    }

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ROLE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int role_id = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE_ID_COL));
                String role_name = cursor.getString(cursor.getColumnIndexOrThrow(ROLE_NAME));

                Role role = new Role(role_id, role_name);
                roles.add(role);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return roles;
    }

    //===========================================================//
    //================= FUNCTIONS FOR COURSES ===================//
    //===========================================================//

    public int getLastInsertedCourseId() {
        int lastId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + COURSE_TABLE, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0);
            }
            cursor.close();
        }
        return lastId;
    }

    public boolean insertCourse(String courseDay, String courseTime, String courseName, String courseType, String price, int duration, int capacity, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_DAY, courseDay);
        contentValues.put(COURSE_TIME, courseTime);
        contentValues.put(COURSE_NAME, courseName);
        contentValues.put(COURSE_TYPE, courseType);
        contentValues.put(COURSE_PRICE, price);
        contentValues.put(COURSE_DURATION, duration);
        contentValues.put(COURSE_CAPACITY, capacity);
        contentValues.put(COURSE_DESCRIPTION, description);
        long result = db.insert(COURSE_TABLE, null, contentValues);
        return result != -1;
    }

    public boolean updateCourse(int courseId, String name, String type, String price, String duration, int capacity, String description, String courseDay, String courseTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_NAME, name);
        contentValues.put(COURSE_TYPE, type);
        contentValues.put(COURSE_PRICE, price);
        contentValues.put(COURSE_DURATION, duration);
        contentValues.put(COURSE_CAPACITY, capacity);
        contentValues.put(COURSE_DESCRIPTION, description);
        contentValues.put(COURSE_DAY, courseDay);
        contentValues.put(COURSE_TIME, courseTime);

        int result = db.update(COURSE_TABLE, contentValues, COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        return result > 0; // Return true if the update was successful
    }

    public boolean deleteCourseInDatabase(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COURSE_TABLE, COURSE_ID + "=?", new String[]{String.valueOf(courseId)}) > 0;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + COURSE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID));
                String courseName = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME));
                String courseType = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE));
                String coursePrice = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_PRICE));
                String courseDuration = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DURATION));
                String courseCapacity = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CAPACITY));
                String courseDescription = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION));
                String courseDay = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY));
                String courseTime = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME));

                Course course = new Course(courseId, courseName, courseType, coursePrice, courseDuration, courseCapacity,
                        courseDescription, courseDay, courseTime);
                courses.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courses;
    }

    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Course course = null;

        // Query to select the course by ID
        Cursor cursor = db.query(COURSE_TABLE, null, COURSE_ID + "=?", new String[]{String.valueOf(courseId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE));
            String price = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_PRICE));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DURATION));
            String capacity = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CAPACITY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION));
            String courseDay = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY));
            String courseTime = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME));

            course = new Course(courseId, name, type, price, duration, capacity, description, courseDay, courseTime);
        }
        if (cursor != null) {
            cursor.close();
        }
        return course;
    }
    //===========================================================//
    //================= FUNCTIONS FOR CLASSES ===================//
    //===========================================================//

    public boolean isClassNameDuplicate(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM classes WHERE name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{className});

        boolean isDuplicate = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isDuplicate;
    }

    public int getLastInsertedClassId() {
        int lastId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + CLASS_TABLE, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0);
            }
            cursor.close();
        }
        return lastId;
    }


    public boolean insertClassInstance(String courseId, String name, String date, String instructor, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLASS_COURSE_ID, courseId);
        contentValues.put(CLASS_NAME, name);
        contentValues.put(CLASS_DATE, date);
        contentValues.put(CLASS_INSTRUCTOR, instructor);
        contentValues.put(CLASS_COMMENTS, comments);
        long result = db.insert(CLASS_TABLE, null, contentValues);
        return result != -1;
    }

    public boolean updateClassInstance(int classId, int courseId, String name, String date, int instructor, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLASS_COURSE_ID, courseId);
        contentValues.put(CLASS_NAME, name);
        contentValues.put(CLASS_DATE, date);
        contentValues.put(CLASS_INSTRUCTOR, instructor);
        contentValues.put(CLASS_COMMENTS, comments);

        int result = db.update(CLASS_TABLE, contentValues, CLASS_ID + "=?", new String[]{String.valueOf(classId)});
        return result > 0;
    }

    public boolean deleteClassInDatabase(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CLASS_TABLE, CLASS_ID + "=?", new String[]{String.valueOf(classId)}) > 0;
    }

    public List<Classes> getAllClasses() {
        List<Classes> classesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CLASS_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID));
                String courseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
                String instructor = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

                Classes classInstance = new Classes(id, courseId, name, date, instructor, comments);
                classesList.add(classInstance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classesList;
    }

    public Classes getClassById(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Classes classInstance = null;

        // Query to select the class by ID
        Cursor cursor = db.query(CLASS_TABLE, null, CLASS_ID + "=?", new String[]{String.valueOf(classId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID));
            String courseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
            String instructor = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
            String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

            classInstance = new Classes(id, courseId, name, date, instructor, comments);
        }

        if (cursor != null) {
            cursor.close();
        }
        return classInstance;
    }

    public List<Classes> getClassesByCourseId(int courseId) {
        List<Classes> classList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            // Query class based on course id
            cursor = db.query( CLASS_TABLE,null,CLASS_COURSE_ID + "=?", new String[]{String.valueOf(courseId)},  null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID));
                    String retrievedCourseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
                    String instructorId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
                    String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

                    Classes classInstance = new Classes(id, retrievedCourseId, name, date, instructorId, comments);
                    classList.add(classInstance);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return classList;
    }



    //===========================================================//
    //================ FUNCTIONS FOR SEARCHING ==================//
    //===========================================================//

    public List<Classes> searchClassesByName(String name) {
        List<Classes> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + CLASS_NAME + " LIKE ?", new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID));
                String courseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
                String className = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
                String instructorId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

                Classes classInstance = new Classes(id, courseId, className, date, instructorId, comments);
                classes.add(classInstance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classes;
    }

    public List<Course> searchCoursesByName(String name) {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_NAME + " LIKE ?", new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID));
                String courseName = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_NAME));
                String courseType = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE));
                String price = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_PRICE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DURATION));
                String capacity = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_CAPACITY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION));
                String courseDay = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY));
                String courseTime = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME));

                Course course = new Course(courseId, courseName, courseType, price, duration, capacity, description, courseDay, courseTime);
                courses.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courses;
    }

    public List<Instructor> searchInstructorsByName(String name) {
        List<Instructor> instructors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + NAME + " LIKE ?", new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID));
                String instructorName = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
                int roleId = cursor.getInt(cursor.getColumnIndexOrThrow(ROLE_ID));

                Instructor instructor = new Instructor(id, instructorName, email, password, roleId);
                instructors.add(instructor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return instructors;
    }

    public List<Classes> searchClassesByInstructor(String instructorName) {
        List<Classes> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                CLASS_TABLE + " WHERE " + CLASS_INSTRUCTOR + " " +
                "IN (SELECT " + USER_ID + " FROM " + USER_TABLE + " WHERE " + NAME + " LIKE ?)", new String[]{"%" + instructorName + "%"});

        if (cursor.moveToFirst()) {
            do {
                int classId = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID));
                String courseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
                String instructorId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

                Classes classItem = new Classes(classId, courseId, name, date, instructorId, comments);
                classes.add(classItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return classes;
    }

    public List<Classes> getClassesByDate(String date) {
        List<Classes> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + CLASS_TABLE + " WHERE " + CLASS_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(CLASS_ID)); // Use getColumnIndexOrThrow
                    String courseId = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COURSE_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                    String classDate = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_DATE));
                    String instructor = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_INSTRUCTOR));
                    String comments = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_COMMENTS));

                    Classes classes = new Classes(id, courseId, name, classDate, instructor, comments);
                    classList.add(classes);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close(); // Ensure cursor is closed to prevent memory leaks
        }

        return classList;
    }

    //===========================================================//
    //=============== FUNCTIONS FOR CLEAR TABLE =================//
    //===========================================================//

    public void clearTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM " + CLASS_TABLE);
            db.execSQL("DELETE FROM " + COURSE_TABLE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

}
