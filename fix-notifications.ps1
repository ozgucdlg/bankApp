Write-Host "Running notification fixer script..." -ForegroundColor Green

# URL of the backend API
$API_URL = "http://localhost:8080/api/notifications"

# Call the fix endpoint
Write-Host "Calling fix-null-read-values endpoint..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "$API_URL/fix-null-read-values" -Method Get

# Add a test notification for the current user to verify functionality
Write-Host "`nCreating a test notification for admin..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "$API_URL/create-test-notification/admin" -Method Get

# Check all notifications for the user
Write-Host "`nListing all notifications for admin..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "$API_URL/all?recipient=admin" -Method Get | ConvertTo-Json -Depth 4

Write-Host "`nScript complete. Please check the output above." -ForegroundColor Green 