db.createUser(
    {
        user: "usr_tournament",
        pwd: "b3tcl1c",
        roles: [
            {
                role: "readWrite",
                db: "tournament"
            }
        ]
    }
);
