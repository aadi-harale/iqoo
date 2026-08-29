$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$package = "com.iqoo.guardian"

function Show-Screen {
    param([string]$uri)
    Write-Host "Opening $uri"
    & $adb shell am start -W -a android.intent.action.VIEW -d "$uri" $package | Out-Null
    Start-Sleep -Seconds 2
}

function Scroll-Down {
    Write-Host "Scrolling down..."
    & $adb shell input swipe 500 1500 500 400 2000
    Start-Sleep -Seconds 1
}

function Tap-Screen {
    Write-Host "Tapping to trigger..."
    & $adb shell input tap 500 1000
    Start-Sleep -Seconds 1
}

Write-Host "🚀 STARTING iQOO GUARDIAN PRESENTATION MODE..."
& $adb shell am force-stop $package
Start-Sleep -Seconds 1

Write-Host "1. Opening Dashboard"
& $adb shell am start -n "$package/.MainActivity" | Out-Null
Start-Sleep -Seconds 3
Scroll-Down

Write-Host "2. Showing Device Hub (Digital Twin)"
Show-Screen "iqoo://guardian/device"
Scroll-Down

Write-Host "3. Deep dive into Battery"
Show-Screen "iqoo://guardian/battery"
Scroll-Down

Write-Host "4. Deep dive into Thermal Radar"
Show-Screen "iqoo://guardian/thermal"
Scroll-Down

Write-Host "5. Showing Chatbot Intelligence"
Show-Screen "iqoo://guardian/chat"
Start-Sleep -Seconds 2

Write-Host "6. Hardware Sensors & Biometrics"
Show-Screen "iqoo://guardian/sensors"
Scroll-Down

Write-Host "7. Privacy Proof (0 Cloud Uploads)"
Show-Screen "iqoo://guardian/privacy"
Scroll-Down

Write-Host "8. Launching Threat Scenario"
Show-Screen "iqoo://guardian/demolab"
Start-Sleep -Seconds 1
Show-Screen "iqoo://guardian/analysis/flashdeals_stealth_drain"

Write-Host "⏳ Watching Analysis sequence..."
Start-Sleep -Seconds 6
Scroll-Down
Start-Sleep -Seconds 3

Write-Host "✅ PRESENTATION COMPLETE!"
