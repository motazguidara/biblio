#!/bin/bash

# Update package lists
sudo apt-get update

# Install MySQL Server
sudo apt-get install -y mysql-server

# Start MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql

# Set root password (replace 'your_root_password' with a strong password)
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_root_password';"

# Run the initialization script
mysql -u root -p'your_root_password' < /media/moataz/Disque\ local/dev\ MG/biblio/bibliotheque_init.sql

echo "Database setup completed successfully!"
