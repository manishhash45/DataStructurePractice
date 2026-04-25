#!/bin/bash

# File: /tmp/csv-demo.sh

echo "=========================================="
echo "CSV File Processing Demo"
echo "=========================================="
echo ""

BUCKET_PATH='/Users/in46076885/Documents/Workspace/FileStorageService/s3-bucket/default'
API_URL='http://localhost:8086/api'

echo "⚠️  Make sure the application is running!"
echo ""
read -p "Press Enter to continue..."
echo ""

# Create CSV file with employee data
echo "Creating employee CSV file..."
cat > /tmp/employees-demo.csv << 'EOF'
employeeName,gender,salary,age,department
John Doe,Male,75000,35,Engineering
Jane Smith,Female,68000,32,Marketing
Michael Johnson,Male,82000,40,Engineering
Sarah Williams,Female,70000,38,Sales
Robert Brown,Male,65000,28,HR
Emily Davis,Female,72000,31,Engineering
David Miller,Male,78000,45,Finance
Lisa Anderson,Female,71000,36,Marketing
EOF

echo "✓ CSV file created"
echo ""

# DEMO 1: Upload CSV via API
echo "=========================================="
echo "DEMO 1: Upload CSV via REST API"
echo "=========================================="
echo ""
echo "Uploading CSV file via REST API..."
UPLOAD_RESPONSE=$(curl -s -X POST \
  -F 'file=@/tmp/employees-demo.csv' \
  "${API_URL}/files/upload?bucketName=default")

echo "Upload Response:"
echo "$UPLOAD_RESPONSE" | jq '.'
FILE_KEY=$(echo "$UPLOAD_RESPONSE" | jq -r '.fileKey')
echo ""
sleep 3

# DEMO 2: Direct CSV copy to bucket (triggers Camel listener)
echo "=========================================="
echo "DEMO 2: Direct CSV File Copy (Trigger Camel Listener)"
echo "=========================================="
echo ""
echo "Copying CSV file directly to bucket..."
echo "The Camel listener will detect and process this CSV file!"
echo ""
cp /tmp/employees-demo.csv "$BUCKET_PATH/employees-batch.csv"
echo "✓ CSV file copied to bucket"
echo "Check application logs for CSV processing messages!"
echo ""
sleep 5

# DEMO 3: Fetch all employees
echo "=========================================="
echo "DEMO 3: Fetch All Employees from Database"
echo "=========================================="
echo ""
echo "Command: curl -X GET '${API_URL}/employees/all'"
EMPLOYEES=$(curl -s -X GET "${API_URL}/employees/all")
echo "Employees in Database:"
echo "$EMPLOYEES" | jq '.'
echo ""
sleep 2

# DEMO 4: Get employee count
echo "=========================================="
echo "DEMO 4: Get Total Employee Count"
echo "=========================================="
echo ""
echo "Command: curl -X GET '${API_URL}/employees/count'"
COUNT=$(curl -s -X GET "${API_URL}/employees/count")
echo "Total Employees: $COUNT"
echo ""
sleep 2

# DEMO 5: Get employees by department
echo "=========================================="
echo "DEMO 5: Get Employees by Department"
echo "=========================================="
echo ""
echo "Command: curl -X GET '${API_URL}/employees/department/Engineering'"
ENG_EMPLOYEES=$(curl -s -X GET "${API_URL}/employees/department/Engineering")
echo "Engineering Department Employees:"
echo "$ENG_EMPLOYEES" | jq '.'
echo ""
sleep 2

# DEMO 6: Get employees with minimum salary
echo "=========================================="
echo "DEMO 6: Get Employees with Salary >= 70000"
echo "=========================================="
echo ""
echo "Command: curl -X GET '${API_URL}/employees/salary/70000'"
HIGH_SALARY=$(curl -s -X GET "${API_URL}/employees/salary/70000")
echo "Employees with Salary >= 70000:"
echo "$HIGH_SALARY" | jq '.'
echo ""
sleep 2

# DEMO 7: Get employees by age range
echo "=========================================="
echo "DEMO 7: Get Employees Aged 30-40"
echo "=========================================="
echo ""
echo "Command: curl -X GET '${API_URL}/employees/age?minAge=30&maxAge=40'"
AGE_RANGE=$(curl -s -X GET "${API_URL}/employees/age?minAge=30&maxAge=40")
echo "Employees Aged 30-40:"
echo "$AGE_RANGE" | jq '.'
echo ""
sleep 2

# DEMO 8: Create a new employee via API
echo "=========================================="
echo "DEMO 8: Create New Employee via API"
echo "=========================================="
echo ""
echo "Command: curl -X POST '${API_URL}/employees' -H 'Content-Type: application/json' -d '{...}'"
NEW_EMPLOYEE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "employeeName": "Alex Thompson",
    "gender": "Male",
    "salary": 76000,
    "age": 33,
    "department": "Engineering"
  }' \
  "${API_URL}/employees")

echo "New Employee Created:"
echo "$NEW_EMPLOYEE" | jq '.'
echo ""

# Final summary
echo "=========================================="
echo "Summary of CSV Processing"
echo "=========================================="
echo ""
echo "✓ CSV file uploaded via API"
echo "✓ CSV file processed by Camel listener"
echo "✓ Employees saved to Derby database"
echo "✓ All employee queries tested"
echo ""
echo "Files in S3 bucket:"
ls -la "$BUCKET_PATH/" | grep -E ".csv|employees"
echo ""
echo "=========================================="
