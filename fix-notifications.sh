#!/bin/bash

echo "Running notification fixer script..."

# URL of the backend API
API_URL="http://localhost:8080/api/notifications"

# Call the fix endpoint
echo "Calling fix-null-read-values endpoint..."
curl -X GET "${API_URL}/fix-null-read-values"

# Add a test notification for the current user to verify functionality
echo -e "\n\nCreating a test notification for admin..."
curl -X GET "${API_URL}/create-test-notification/admin"

# Check all notifications for the user
echo -e "\n\nListing all notifications for admin..."
curl -X GET "${API_URL}/all?recipient=admin"

echo -e "\n\nScript complete. Please check the output above." 