$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080'

function Login($email) {
  $body = @{ email = $email; password = 'demo123' } | ConvertTo-Json
  return Invoke-RestMethod -Uri "$base/api/auth/login" -Method POST -ContentType 'application/json' -Body $body
}

function Get-WithUser($path, $userId) {
  return Invoke-RestMethod -Uri "$base$path" -Headers @{ 'X-Demo-User-Id' = $userId }
}

function Post-WithUser($path, $userId, $json) {
  return Invoke-RestMethod -Uri "$base$path" -Method POST -ContentType 'application/json' -Headers @{ 'X-Demo-User-Id' = $userId } -Body $json
}

Write-Host '=== Parent1: list campaigns ==='
$p1 = Login 'parent1@demo.local'
$camps = Get-WithUser '/api/parent/campaigns' $p1.userId
if ($camps.Count -lt 5) { throw "Expected >=5 campaigns, got $($camps.Count)" }

Write-Host '=== Parent1: existing submission for Chris ==='
$subs = Get-WithUser '/api/parent/campaigns/1/submissions' $p1.userId
$chris = $subs | Where-Object { $_.studentName -eq 'Chris Chan' -and $_.status -eq 'SUBMITTED' }
if (-not $chris) { throw 'Chris LIMITED submission missing from seed' }

Write-Host '=== Parent1: submit Sam YES on campaign 1 ==='
Post-WithUser '/api/parent/submissions' $p1.userId (@{ campaignId = 1; studentId = 2; decision = 'YES'; scopeNote = $null } | ConvertTo-Json) | Out-Null

Write-Host '=== Parent2: resubmit Parker after withdraw ==='
$p2 = Login 'parent2@demo.local'
Post-WithUser '/api/parent/submissions' $p2.userId (@{ campaignId = 1; studentId = 10; decision = 'YES'; scopeNote = $null } | ConvertTo-Json) | Out-Null

Write-Host '=== School Riverside: see submissions ==='
$s1 = Login 'school@riverside.demo'
$rc = Get-WithUser '/api/school/campaigns' $s1.userId
if ($rc.Count -lt 3) { throw "Riverside expected 3 campaigns" }
$rows = Get-WithUser '/api/school/campaigns/1/submissions' $s1.userId
if ($rows.Count -lt 5) { throw "Expected seeded+new submissions on campaign 1, got $($rows.Count)" }

Write-Host '=== School Harbour: only KG campaign ==='
$s2 = Login 'school@harbour.demo'
$hc = Get-WithUser '/api/school/campaigns' $s2.userId
if ($hc.Count -ne 1 -or $hc[0].qrToken -ne 'harbour-openday-2027') { throw 'Harbour school campaign mismatch' }

Write-Host '=== School Summit ==='
$s3 = Login 'school@summit.demo'
$sc = Get-WithUser '/api/school/campaigns' $s3.userId
if ($sc.Count -ne 1) { throw 'Summit expected 1 campaign' }

Write-Host '=== Parent blocked from school API ==='
try {
  Get-WithUser '/api/school/campaigns' $p1.userId
  throw 'Parent should not access school API'
} catch {
  if ($_.Exception.Response.StatusCode.value__ -ne 403) { throw $_ }
}

Write-Host '=== School blocked from parent students ==='
try {
  Get-WithUser '/api/parent/students' $s1.userId
  throw 'School should not access parent API'
} catch {
  if ($_.Exception.Response.StatusCode.value__ -ne 403) { throw $_ }
}

Write-Host '=== Closed campaign rejects submit ==='
try {
  Post-WithUser '/api/parent/submissions' $p1.userId (@{ campaignId = 5; studentId = 2; decision = 'YES'; scopeNote = $null } | ConvertTo-Json)
  throw 'Closed campaign should reject'
} catch {
  if ($_.Exception.Response.StatusCode.value__ -ne 400) { throw $_ }
}

Write-Host 'ALL ROLE SMOKE TESTS PASSED'
