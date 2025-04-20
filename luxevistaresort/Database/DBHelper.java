package com.luxevistaresort.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luxevistaresort.Models.Booking;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.Models.ServiceReservation;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LuxeVista.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";

    public static final String TABLE_BOOKINGS = "bookings";

    public static final String TABLE_ROOMS = "rooms";

    public static final String TABLE_SERVICE_RESERVATIONS = "service_reservations";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT, " +
                "email TEXT, " +
                "number TEXT, " +
                "password TEXT," +
                "image TEXT)";
        db.execSQL(createTable);

        String createBookingTable = "CREATE TABLE bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "num_rooms INTEGER," +
                "nights INTEGER," +
                "total TEXT," +
                "checkin_date TEXT," +
                "checkout_date TEXT," +
                "date TEXT," +
                "user_id INTEGER, " +
                "room_id INTEGER, " +
                "status INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY(room_id) REFERENCES rooms(id) ON DELETE CASCADE)";
        db.execSQL(createBookingTable);

        String createServiceReservationTable = "CREATE TABLE service_reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "service_type TEXT," +
                "date TEXT," +
                "time TEXT," +
                "guest_name TEXT," +
                "user_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";

        db.execSQL(createServiceReservationTable);

        String createRoomTable = "CREATE TABLE rooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "room_type TEXT," +
                "price TEXT," +
                "image TEXT," +
                "description TEXT," +
                "rating TEXT," +
                "availability INT)";

        db.execSQL(createRoomTable);

        insertSampleRooms(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean insertUser(String username, String email, String number, String password, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("number", number);
        values.put("password", password);
        values.put("image", imagePath);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
    }

    public boolean updateUser(String oldEmail, String newName, String newEmail, String newNumber, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newName);
        values.put("email", newEmail);
        values.put("number", newNumber);
        values.put("image", imagePath);

        int result = db.update("users", values, "email = ?", new String[]{oldEmail});
        return result > 0;
    }

    // For testing purpose
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS, null);
    }

    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Verify old password for the given email
    public boolean verifyUserPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Update user password
    public boolean updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "email = ?", new String[]{email});
        return rows > 0;
    }

    public void deleteUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Optional: get user ID to delete related bookings too
        Cursor cursor = getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

            // Delete related bookings
            db.delete("bookings", "user_id=?", new String[]{String.valueOf(userId)});
            db.delete("service_reservations", "user_id=?", new String[]{String.valueOf(userId)});
        }

        // Delete user
        db.delete("users", "email=?", new String[]{email});
        db.close();
    }


    // Insert Booking to DB
    public boolean insertBooking(int userId, int roomId, int numRooms, int nights,
                              String totalCost, String checkinDate, String checkoutDate, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("room_id", roomId);
        values.put("num_rooms", numRooms);
        values.put("nights", nights);
        values.put("total", totalCost);
        values.put("checkin_date", checkinDate);
        values.put("checkout_date", checkoutDate);
        values.put("date", date);
        values.put("status", "1");

        long result = db.insert("bookings", null, values);
        return result != -1;
    }

    public void updateRoomAvailability(int roomId, String availability) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("availability", availability);  // "1" or "0"
        db.update("rooms", values, "id = ?", new String[]{String.valueOf(roomId)});
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL Query to join the bookings and rooms tables on room_id
        String query = "SELECT b.id AS id, b.user_id, b.room_id, b.date, b.total, b.nights," +
                "b.status, r.name, r.image, r.rating " +
                "FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.user_id = ? AND b.status == 1 ";

        // Execute the query with the user_id as parameter
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Create a new Booking object
                Booking booking = new Booking();

                // Set values from the bookings table
                booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                booking.setRoomId(cursor.getInt(cursor.getColumnIndexOrThrow("room_id")));
                booking.setBookedDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                booking.setRoomPrice(cursor.getString(cursor.getColumnIndexOrThrow("total")));

                // Set values from the rooms table
                booking.setRoomTitle(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                booking.setRoomImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                booking.setRating(cursor.getString(cursor.getColumnIndexOrThrow("rating")));
                booking.setNights(cursor.getInt(cursor.getColumnIndexOrThrow("nights")));
                booking.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));

                // Add the booking to the list
                list.add(booking);
            } while (cursor.moveToNext());

            cursor.close();  // Close the cursor after use
        }

        return list;
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
            return userId;
        } else {
            cursor.close();
            return -1;  // Return -1 if user is not found
        }
    }


    // Add Service Reservation
    public long addServiceReservation(ServiceReservation reservation, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("service_type", reservation.getServiceType());
        values.put("date", reservation.getDate());
        values.put("time", reservation.getTime());
        values.put("guest_name", reservation.getGuestName());
        values.put("user_id", userId);
        return db.insert("service_reservations", null, values);
    }

    public boolean isServiceAvailable(String service, String date, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM service_reservations WHERE service_type = ? AND date = ? AND time = ?",
                new String[]{service, date, time}
        );
        boolean available = !cursor.moveToFirst();
        cursor.close();
        return available;
    }

    public List<ServiceReservation> getAllServiceReservations() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ServiceReservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM service_reservations";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String service = cursor.getString(cursor.getColumnIndexOrThrow("service_type"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String guestName = cursor.getString(cursor.getColumnIndexOrThrow("guest_name"));
                Integer userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));

                ServiceReservation reservation = new ServiceReservation(userId,service, date, time, guestName);
                reservations.add(reservation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return reservations;
    }

    // Get Service for History
    public List<ServiceReservation> getServiceReservationsByUserId(int userId) {
        List<ServiceReservation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM service_reservations WHERE user_id = ? ORDER BY date DESC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String serviceType = cursor.getString(cursor.getColumnIndexOrThrow("service_type"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String guestName = cursor.getString(cursor.getColumnIndexOrThrow("guest_name"));

                ServiceReservation res = new ServiceReservation(userId,serviceType, date, time, guestName);
                list.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private void insertRoom(SQLiteDatabase db, String name, String room_type, String price,String image, String description,String rating,String availability) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("room_type", room_type);
        values.put("price", price);
        values.put("image", image);
        values.put("description", description);
        values.put("rating", rating);
        values.put("availability", availability);
        db.insert(TABLE_ROOMS, null, values);
    }

    private void insertSampleRooms(SQLiteDatabase db) {

        insertRoom(db, "King Deluxe", "Deluxe", "299.99", "king_hilton_deluxe.png", "Take in the stunning ocean view from this stylish guest room, featuring 1 king bed and a seating area with sofa.","3.5" ,"1");
        insertRoom(db, "King Deluxe Sea View Room", "Deluxe", "249.99", "kingdeluxeseaviewbedroom01.png", "Enjoy breathtaking ocean views from this elegant guest room which features a luxurious king bed and a comfortable seating space complete with a sofa.","4.0","1");
        insertRoom(db, "King Superior Room", "Superior", "350", "king_superiror_room.png", "Enjoy stunning vistas from this elegant guest room, which boasts a king-sized bed and a cozy seating area featuring a sofa.", "4.1","1");
        insertRoom(db, "Twin Deluxe", "Deluxe", "450", "twin_hilton_deluxe.png", "Ocean view, upgraded amenities, seating area with sofa", "4.2","1");
        insertRoom(db, "Twin Deluxe Sea View Room", "Superior", "550", "twindeluxebedroomseaview02.png", "Experience breathtaking vistas of the ocean from this spacious guest room, featuring twin beds and a comfortable seating arrangement complete with a sofa.", "4.1","1");
        insertRoom(db, "King Corner Suite", "Suite", "170", "king_corner_suite.png", "Views of Colombo, Executive Lounge access, high floor, corner suite, 61 sq. m./656 sq. ft., sofa", "3.9","1");
        insertRoom(db, "Twin Superior Room", "Superior", "499.99", "twin_superior_room.png", "Enjoy awe-inspiring vistas from this guest room, which boasts twin beds and a cozy seating area complemented by a sofa.", "4.1","1");
        insertRoom(db, "King Deluxe Suite", "Superior", "350", "king_deluxe_suite.png", "Sweeping Colombo City and CBD views. This 80.14 sq. m. one bedroom suite boasts a separate living and large entertainment area.", "4.4","1");
        insertRoom(db, "King Business Suite", "Suite", "199.99", "king_business_suite.png", "Ocean view, Executive Lounge access, 61 sq. m./656 sq. ft., seating area", "4.3","1");
        insertRoom(db, "King Executive Room", "Executive", "189.99", "king_executive.png", "Ocean view, Executive Lounge access, larger room, seating area with sofa, work area", "4.5","1");
        insertRoom(db, "Twin Executive Room", "Executive", "179.99", "twin_executive.png", "Ocean view, Executive Lounge access, larger room, seating area with sofa, work area", "4.2","1");
    }

    public Room getRoomById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Room room = null;
        Cursor cursor = db.rawQuery("SELECT * FROM rooms WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String imageResId = cursor.getString(cursor.getColumnIndexOrThrow("image"));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"));
            room = new Room(id, title, price, description, imageResId, rating);
        }
        cursor.close();
        return room;
    }

    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rooms", null);

        if (cursor.moveToFirst()) {
            do {
                Room room = new Room();
                room.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                room.setRoomTitle(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                room.setType(cursor.getString(cursor.getColumnIndexOrThrow("room_type")));
                room.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));
                room.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                room.setShortDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                room.setRating(cursor.getString(cursor.getColumnIndexOrThrow("rating")));
                room.setAvailability(cursor.getString(cursor.getColumnIndexOrThrow("availability")));

                roomList.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomList;
    }

    public List<Room> getFeaturedRooms() {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM rooms LIMIT 5", null);

        if (cursor.moveToFirst()) {
            do {
                Room room = new Room();
                room.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                room.setRoomTitle(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                room.setShortDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                room.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                room.setPrice(cursor.getString(cursor.getColumnIndexOrThrow("price")));
                roomList.add(room);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomList;
    }

    public void updateBookingStatus(int bookingId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update("bookings", values, "id = ?", new String[]{String.valueOf(bookingId)});
        db.close();
    }


}
