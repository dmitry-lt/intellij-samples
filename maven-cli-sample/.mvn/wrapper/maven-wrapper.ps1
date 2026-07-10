$ErrorActionPreference = "Stop"

$propertiesPath = Join-Path $PSScriptRoot "maven-wrapper.properties"
if (-not (Test-Path -LiteralPath $propertiesPath)) {
    throw "Missing Maven Wrapper properties: $propertiesPath"
}

$properties = @{}
Get-Content -LiteralPath $propertiesPath | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#")) {
        $parts = $line.Split("=", 2)
        if ($parts.Length -eq 2) {
            $properties[$parts[0].Trim()] = $parts[1].Trim()
        }
    }
}

$distributionUrl = $properties["distributionUrl"]
if (-not $distributionUrl) {
    throw "distributionUrl is not configured in $propertiesPath"
}

$archiveName = [System.IO.Path]::GetFileName(([Uri]$distributionUrl).AbsolutePath)
$distributionName = [System.IO.Path]::GetFileNameWithoutExtension($archiveName)
if ($distributionName.EndsWith("-bin")) {
    $distributionName = $distributionName.Substring(0, $distributionName.Length - 4)
}

$mavenUserHome = $env:MAVEN_USER_HOME
if (-not $mavenUserHome) {
    $mavenUserHome = Join-Path ([Environment]::GetFolderPath("UserProfile")) ".m2"
}
$mavenHome = Join-Path $mavenUserHome "wrapper\dists\$distributionName"
$mavenCommand = Join-Path $mavenHome "bin\mvn.cmd"

if (-not (Test-Path -LiteralPath $mavenCommand)) {
    $tempDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("mvnw-" + [Guid]::NewGuid())
    New-Item -ItemType Directory -Path $tempDirectory | Out-Null
    try {
        $archivePath = Join-Path $tempDirectory $archiveName
        Write-Host "Downloading Maven from $distributionUrl"
        Invoke-WebRequest -UseBasicParsing -Uri $distributionUrl -OutFile $archivePath

        $expectedHash = $properties["distributionSha256Sum"]
        if ($expectedHash) {
            $actualHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $archivePath).Hash.ToLowerInvariant()
            if ($actualHash -ne $expectedHash.ToLowerInvariant()) {
                throw "Maven distribution SHA-256 mismatch. Expected $expectedHash but got $actualHash."
            }
        }

        Expand-Archive -LiteralPath $archivePath -DestinationPath $tempDirectory
        $extracted = Get-ChildItem -LiteralPath $tempDirectory -Directory |
            Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName "bin\mvn.cmd") } |
            Select-Object -First 1
        if (-not $extracted) {
            throw "The Maven archive did not contain bin\mvn.cmd"
        }

        New-Item -ItemType Directory -Force -Path (Split-Path -Parent $mavenHome) | Out-Null
        if (-not (Test-Path -LiteralPath $mavenHome)) {
            Move-Item -LiteralPath $extracted.FullName -Destination $mavenHome
        }
    }
    finally {
        Remove-Item -LiteralPath $tempDirectory -Recurse -Force -ErrorAction SilentlyContinue
    }
}

& $mavenCommand @args
exit $LASTEXITCODE
