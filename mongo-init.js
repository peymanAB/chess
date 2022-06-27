db.createUser(
        {
            user: "chess_user",
            pwd: "123456",
            roles: [
                {
                    role: "readWrite",
                    db: "chess_db"
                }
            ]
        }
);
