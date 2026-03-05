package com.example.errors

import io.ktor.http.HttpStatusCode

internal fun errorHtml(
    status: HttpStatusCode,
    detail: String?,
    instance: String?,
): String {
    val (headline, quip, illustration) = errorPersonality(status)
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Error ${status.value}: ${status.description}</title>
          <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body {
              background: #0d0d1a;
              color: #e0e0ff;
              font-family: 'Courier New', monospace;
              min-height: 100vh;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              gap: 1.5rem;
              padding: 2rem;
              text-align: center;
            }
            .code {
              font-size: 7rem;
              font-weight: bold;
              color: #ff4466;
              text-shadow: 0 0 30px #ff446699, 0 0 60px #ff446633;
              line-height: 1;
              animation: pulse 2s ease-in-out infinite;
            }
            @keyframes pulse {
              0%, 100% { text-shadow: 0 0 30px #ff446699, 0 0 60px #ff446633; }
              50%       { text-shadow: 0 0 50px #ff4466cc, 0 0 80px #ff446655; }
            }
            .headline { font-size: 1.4rem; color: #cc88ff; }
            .quip { font-size: 0.9rem; color: #666; font-style: italic; max-width: 420px; }
            .detail {
              font-size: 0.8rem; color: #555;
              background: #1a1a2e; border: 1px solid #333; border-radius: 6px;
              padding: 0.5rem 1rem; max-width: 500px; word-break: break-all;
            }
            .home {
              margin-top: 0.5rem; color: #00ff88; text-decoration: none;
              font-size: 0.9rem; border: 1px solid #00ff8844;
              padding: 0.5rem 1.5rem; border-radius: 6px;
            }
          </style>
        </head>
        <body>
          <div class="code">${status.value}</div>
          $illustration
          <div class="headline">$headline</div>
          <div class="quip">$quip</div>
          ${if (detail != null) """<div class="detail">$detail</div>""" else ""}
          ${if (instance != null) """<div class="detail" style="color:#444">&#x21a9; $instance</div>""" else ""}
          <a class="home" href="/">&#x2190; Take me somewhere safe</a>
        </body>
        </html>
        """.trimIndent()
}

private data class ErrorPersonality(
    val headline: String,
    val quip: String,
    val illustration: String,
)

private fun errorPersonality(status: HttpStatusCode): ErrorPersonality =
    when (status) {
        HttpStatusCode.NotFound ->
            ErrorPersonality(
                headline = "Lost in the void.",
                quip = "The resource you seek has ascended to a higher plane. Or was never here. Probably the latter.",
                illustration = svgNotFound(),
            )
        HttpStatusCode.BadRequest ->
            ErrorPersonality(
                headline = "That request made zero sense.",
                quip = "We read it three times. Asked a colleague. Called our mum. Still confused.",
                illustration = svgBadRequest(),
            )
        HttpStatusCode.MethodNotAllowed ->
            ErrorPersonality(
                headline = "Nope. Absolutely not.",
                quip = "We respect the hustle, but that HTTP method is not welcome here.",
                illustration = svgMethodNotAllowed(),
            )
        HttpStatusCode.InternalServerError ->
            ErrorPersonality(
                headline = "Something exploded. Internally.",
                quip = "The engineers have been paged. They are currently in denial. Please stand by.",
                illustration = svgInternalServerError(),
            )
        else ->
            ErrorPersonality(
                headline = "Well, this is awkward.",
                quip = "Something went sideways. The logs are, as always, not helpful.",
                illustration = svgGeneric(status.value),
            )
    }

// 404 — robot with a searchlight, sweeping left and right, finding nothing
private fun svgNotFound() =
    """
    <svg width="200" height="190" viewBox="0 0 200 190" xmlns="http://www.w3.org/2000/svg">
      <!-- Stars -->
      <circle cx="28"  cy="18" r="1.5" fill="white"><animate attributeName="opacity" values="0.7;0.1;0.7" dur="2.1s" repeatCount="indefinite"/></circle>
      <circle cx="172" cy="30" r="1"   fill="white"><animate attributeName="opacity" values="0.4;0.9;0.4" dur="1.7s" repeatCount="indefinite"/></circle>
      <circle cx="58"  cy="8"  r="1"   fill="white"><animate attributeName="opacity" values="0.9;0.2;0.9" dur="3.0s" repeatCount="indefinite"/></circle>
      <circle cx="152" cy="12" r="1.5" fill="white"><animate attributeName="opacity" values="0.5;1.0;0.5" dur="2.5s" repeatCount="indefinite"/></circle>

      <!-- Body group, bobs gently -->
      <g>
        <animateTransform attributeName="transform" type="translate"
          values="0 0;0 4;0 0;0 -4;0 0" dur="3s" repeatCount="indefinite"/>

        <!-- Legs -->
        <rect x="78"  y="152" width="18" height="24" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>
        <rect x="104" y="152" width="18" height="24" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>

        <!-- Body -->
        <rect x="60" y="94" width="80" height="62" rx="10" fill="#16163a" stroke="#4444aa" stroke-width="2"/>
        <text x="100" y="133" text-anchor="middle" font-size="26" fill="#cc88ff" font-family="Courier New" font-weight="bold">?</text>

        <!-- Right arm - on hip -->
        <rect x="140" y="100" width="18" height="36" rx="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(15 149 100)"/>

        <!-- Left arm - raised holding flashlight -->
        <rect x="34" y="88" width="18" height="46" rx="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(-35 43 92)"/>

        <!-- Flashlight body -->
        <rect x="10" y="62" width="24" height="12" rx="4" fill="#888" transform="rotate(-35 22 68)"/>

        <!-- Flashlight beam - sweeps back and forth -->
        <polygon points="4,78 -30,145 22,145" fill="#ffff0018" transform="rotate(-35 4 78)">
          <animateTransform attributeName="transform" type="rotate"
            values="-55 4 78;-10 4 78;-55 4 78" dur="2.5s" repeatCount="indefinite" additive="sum"/>
        </polygon>

        <!-- Head -->
        <rect x="65" y="30" width="70" height="66" rx="12" fill="#1e1e3f" stroke="#4444aa" stroke-width="2"/>

        <!-- Antenna -->
        <line x1="100" y1="30" x2="100" y2="14" stroke="#888" stroke-width="3"/>
        <circle cx="100" cy="12" r="5" fill="#cc88ff">
          <animate attributeName="fill" values="#cc88ff;#ffffff;#cc88ff" dur="1.5s" repeatCount="indefinite"/>
        </circle>

        <!-- Left eye - squinting/scanning -->
        <rect x="74" y="46" width="20" height="14" rx="3" fill="#001100"/>
        <rect x="77" y="49" width="14" height="8" rx="2" fill="#00ff88">
          <animate attributeName="width" values="14;2;14" dur="3.5s" repeatCount="indefinite"/>
          <animate attributeName="x" values="77;84;77" dur="3.5s" repeatCount="indefinite"/>
        </rect>

        <!-- Right eye - blinks slowly -->
        <rect x="106" y="46" width="20" height="14" rx="3" fill="#001100"/>
        <rect x="109" y="49" width="14" height="8" rx="2" fill="#00ff88">
          <animate attributeName="height" values="8;1;8" dur="4s" begin="0.5s" repeatCount="indefinite"/>
          <animate attributeName="y"      values="49;53;49" dur="4s" begin="0.5s" repeatCount="indefinite"/>
        </rect>

        <!-- Mouth - flat, perplexed -->
        <rect x="80" y="78" width="40" height="5" rx="2" fill="#444"/>
      </g>
    </svg>
    """.trimIndent()

// 400 — robot shaking its head, X eyes, facepalming hand
private fun svgBadRequest() =
    """
    <svg width="200" height="190" viewBox="0 0 200 190" xmlns="http://www.w3.org/2000/svg">
      <!-- Legs -->
      <rect x="78"  y="158" width="18" height="28" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>
      <rect x="104" y="158" width="18" height="28" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>

      <!-- Body -->
      <rect x="62" y="96" width="76" height="66" rx="10" fill="#16163a" stroke="#4444aa" stroke-width="2"/>

      <!-- Chest LED - angry red/orange flash -->
      <circle cx="100" cy="124" r="7" fill="#ff4400">
        <animate attributeName="fill" values="#ff4400;#ffcc00;#ff4400" dur="0.45s" repeatCount="indefinite"/>
        <animate attributeName="r"    values="7;9;7"                   dur="0.45s" repeatCount="indefinite"/>
      </circle>

      <!-- Left arm - limp at side -->
      <rect x="44" y="102" width="18" height="42" rx="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(12 53 102)"/>

      <!-- Right arm - raised for facepalm -->
      <rect x="138" y="86" width="18" height="52" rx="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(-68 147 90)"/>

      <!-- Facepalm hand -->
      <ellipse cx="78" cy="70" rx="28" ry="17" fill="#1e1e3f" stroke="#4444aa" stroke-width="2"/>
      <line x1="58" y1="58" x2="52" y2="46" stroke="#4444aa" stroke-width="3" stroke-linecap="round"/>
      <line x1="68" y1="54" x2="64" y2="41" stroke="#4444aa" stroke-width="3" stroke-linecap="round"/>
      <line x1="79" y1="52" x2="79" y2="38" stroke="#4444aa" stroke-width="3" stroke-linecap="round"/>
      <line x1="90" y1="54" x2="94" y2="42" stroke="#4444aa" stroke-width="3" stroke-linecap="round"/>

      <!-- Head - shaking side to side -->
      <g>
        <animateTransform attributeName="transform" type="translate"
          values="-5 0;5 0;-5 0" dur="0.38s" repeatCount="indefinite"/>

        <rect x="66" y="24" width="68" height="66" rx="12" fill="#1e1e3f" stroke="#4444aa" stroke-width="2"/>

        <!-- Antenna -->
        <line x1="100" y1="24" x2="100" y2="8" stroke="#888" stroke-width="3"/>
        <circle cx="100" cy="6" r="5" fill="#ff4400">
          <animate attributeName="fill" values="#ff4400;#ffcc00;#ff4400" dur="0.3s" repeatCount="indefinite"/>
        </circle>

        <!-- X eyes -->
        <text x="80"  y="60" font-size="18" fill="#ff4466" font-family="Courier New" font-weight="bold">X</text>
        <text x="104" y="60" font-size="18" fill="#ff4466" font-family="Courier New" font-weight="bold">X</text>

        <!-- Mouth - wavy frustrated -->
        <path d="M78 76 Q87 70 96 76 Q106 82 114 76" stroke="#ff8800" stroke-width="2.5" fill="none" stroke-linecap="round"/>
      </g>

      <!-- Frustration sparks orbiting -->
      <g>
        <animateTransform attributeName="transform" type="rotate" from="0 166 38" to="360 166 38" dur="1.4s" repeatCount="indefinite"/>
        <line x1="166" y1="22" x2="166" y2="12" stroke="#ffcc00" stroke-width="2.5" stroke-linecap="round"/>
        <line x1="179" y1="28" x2="187" y2="20" stroke="#ffcc00" stroke-width="2.5" stroke-linecap="round"/>
        <line x1="182" y1="42" x2="192" y2="42" stroke="#ffcc00" stroke-width="2.5" stroke-linecap="round"/>
      </g>
    </svg>
    """.trimIndent()

// 405 — robot holding a STOP sign with stern eyebrows
private fun svgMethodNotAllowed() =
    """
    <svg width="200" height="190" viewBox="0 0 200 190" xmlns="http://www.w3.org/2000/svg">
      <!-- Legs -->
      <rect x="78"  y="158" width="18" height="28" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>
      <rect x="104" y="158" width="18" height="28" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>

      <!-- Body -->
      <rect x="62" y="96" width="76" height="66" rx="10" fill="#16163a" stroke="#4444aa" stroke-width="2"/>
      <rect x="75" y="112" width="50" height="32" rx="4" fill="#0a0a1f" stroke="#333"/>
      <text x="100" y="130" text-anchor="middle" font-size="9"  fill="#888" font-family="Courier New">METHOD</text>
      <text x="100" y="142" text-anchor="middle" font-size="8"  fill="#555" font-family="Courier New">NOT ALLOWED</text>

      <!-- Left arm - hand on hip -->
      <rect x="44" y="102" width="18" height="42" rx="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(18 53 102)"/>

      <!-- Right arm - raised with sign -->
      <rect x="138" y="86" width="16" height="54" rx="7" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(-44 146 92)"/>

      <!-- STOP sign -->
      <polygon points="156,20 174,20 186,32 186,52 174,64 156,64 144,52 144,32" fill="#cc0000" stroke="#ff2200" stroke-width="2"/>
      <text x="165" y="48" text-anchor="middle" font-size="14" fill="white" font-family="Courier New" font-weight="bold">NO</text>

      <!-- Head -->
      <rect x="66" y="24" width="68" height="66" rx="12" fill="#1e1e3f" stroke="#4444aa" stroke-width="2"/>

      <!-- Antenna - blinking red -->
      <line x1="100" y1="24" x2="100" y2="8" stroke="#888" stroke-width="3"/>
      <circle cx="100" cy="6" r="5" fill="#cc0000">
        <animate attributeName="opacity" values="1;0.2;1" dur="0.8s" repeatCount="indefinite"/>
      </circle>

      <!-- Stern angled eyebrows -->
      <line x1="74"  y1="40" x2="98"  y2="35" stroke="#ff4466" stroke-width="2.5" stroke-linecap="round"/>
      <line x1="102" y1="35" x2="126" y2="40" stroke="#ff4466" stroke-width="2.5" stroke-linecap="round"/>

      <!-- Eyes -->
      <rect x="74"  y="44" width="22" height="14" rx="3" fill="#001100"/>
      <rect x="77"  y="47" width="16" height="8"  rx="2" fill="#00ff88"/>
      <rect x="104" y="44" width="22" height="14" rx="3" fill="#001100"/>
      <rect x="107" y="47" width="16" height="8"  rx="2" fill="#00ff88"/>

      <!-- Mouth - firm flat line -->
      <rect x="82" y="76" width="36" height="5" rx="2" fill="#ff4466"/>
    </svg>
    """.trimIndent()

// 500 — server rack on fire with "this is fine" dog in the corner
private fun svgInternalServerError() =
    """
    <svg width="200" height="190" viewBox="0 0 200 190" xmlns="http://www.w3.org/2000/svg">
      <!-- Server rack -->
      <rect x="48" y="86" width="104" height="96" rx="6" fill="#1a1a1a" stroke="#333" stroke-width="2"/>

      <!-- Server unit 1 - OK -->
      <rect x="56" y="96"  width="88" height="18" rx="3" fill="#0a0a1f" stroke="#333"/>
      <rect x="62" y="100" width="6"  height="10" rx="1" fill="#00ff88">
        <animate attributeName="fill" values="#00ff88;#ffffff;#00ff88" dur="0.6s" repeatCount="indefinite"/>
      </rect>
      <text x="148" y="109" font-size="7" fill="#444" font-family="Courier New">SRV-01</text>

      <!-- Server unit 2 - warning -->
      <rect x="56" y="118" width="88" height="18" rx="3" fill="#0a0a1f" stroke="#333"/>
      <rect x="62" y="122" width="6"  height="10" rx="1" fill="#ff8800">
        <animate attributeName="fill" values="#ff8800;#ffcc00;#ff8800" dur="0.35s" repeatCount="indefinite"/>
      </rect>
      <text x="148" y="131" font-size="7" fill="#444" font-family="Courier New">SRV-02</text>

      <!-- Server unit 3 - on fire -->
      <rect x="56" y="140" width="88" height="18" rx="3" fill="#0a0a1f" stroke="#333"/>
      <rect x="62" y="144" width="6"  height="10" rx="1" fill="#ff0000">
        <animate attributeName="opacity" values="1;0;1" dur="0.18s" repeatCount="indefinite"/>
      </rect>
      <text x="148" y="153" font-size="7" fill="#ff4400" font-family="Courier New">ERR ERR</text>

      <!-- Server unit 4 - definitely fine -->
      <rect x="56" y="162" width="88" height="14" rx="3" fill="#0a0a1f" stroke="#333"/>
      <text x="100" y="172" text-anchor="middle" font-size="6" fill="#333" font-family="Courier New">DEFINITELY FINE</text>

      <!-- Fire - back layer (large, slow) -->
      <path d="M72,86 C66,64 55,58 63,38 C68,52 74,46 70,28 C79,42 84,36 82,18
               C91,34 94,26 96,10 C98,26 101,34 110,18 C108,36 113,42 122,28
               C118,46 124,52 129,38 C137,58 126,64 128,86 Z" fill="#ff6600" opacity="0.75">
        <animate attributeName="d"
          values="M72,86 C66,64 55,58 63,38 C68,52 74,46 70,28 C79,42 84,36 82,18 C91,34 94,26 96,10 C98,26 101,34 110,18 C108,36 113,42 122,28 C118,46 124,52 129,38 C137,58 126,64 128,86 Z;
                  M72,86 C64,66 58,56 66,34 C72,48 76,42 72,24 C82,38 86,30 85,14 C92,28 96,22 96,10 C96,22 100,28 107,14 C106,30 110,38 120,24 C116,42 120,48 126,34 C134,56 128,66 128,86 Z;
                  M72,86 C66,64 55,58 63,38 C68,52 74,46 70,28 C79,42 84,36 82,18 C91,34 94,26 96,10 C98,26 101,34 110,18 C108,36 113,42 122,28 C118,46 124,52 129,38 C137,58 126,64 128,86 Z"
          dur="0.55s" repeatCount="indefinite"/>
      </path>

      <!-- Fire - middle layer -->
      <path d="M78,86 C74,68 66,62 72,48 C77,60 81,54 79,40 C86,52 90,46 89,32
               C95,44 98,38 96,24 C98,38 101,44 107,32 C106,46 110,52 117,40
               C115,54 119,60 124,48 C130,62 122,68 118,86 Z" fill="#ff8800">
        <animate attributeName="d"
          values="M78,86 C74,68 66,62 72,48 C77,60 81,54 79,40 C86,52 90,46 89,32 C95,44 98,38 96,24 C98,38 101,44 107,32 C106,46 110,52 117,40 C115,54 119,60 124,48 C130,62 122,68 118,86 Z;
                  M78,86 C72,70 67,60 74,46 C80,58 83,52 81,38 C88,50 92,44 91,30 C96,42 99,36 96,24 C93,36 96,42 103,30 C102,44 106,50 113,38 C111,52 114,58 121,46 C128,60 122,70 118,86 Z;
                  M78,86 C74,68 66,62 72,48 C77,60 81,54 79,40 C86,52 90,46 89,32 C95,44 98,38 96,24 C98,38 101,44 107,32 C106,46 110,52 117,40 C115,54 119,60 124,48 C130,62 122,68 118,86 Z"
          dur="0.48s" begin="0.12s" repeatCount="indefinite"/>
      </path>

      <!-- Fire - inner hot core -->
      <path d="M84,86 C82,72 76,66 80,54 C84,64 88,58 87,46 C92,56 95,50 94,38
               C98,48 100,44 96,32 C92,44 94,48 98,38 C97,50 100,56 105,46
               C104,58 108,64 112,54 C116,66 110,72 108,86 Z" fill="#ffee00">
        <animate attributeName="opacity" values="0.9;0.55;0.9" dur="0.38s" repeatCount="indefinite"/>
      </path>

      <!-- Floating sparks -->
      <circle cx="86"  cy="20" r="2"   fill="#ffee00">
        <animate attributeName="cy"      values="20;0;20"     dur="1.1s" repeatCount="indefinite"/>
        <animate attributeName="opacity" values="0.9;0;0.9"   dur="1.1s" repeatCount="indefinite"/>
        <animate attributeName="cx"      values="86;82;86"    dur="1.1s" repeatCount="indefinite"/>
      </circle>
      <circle cx="110" cy="14" r="1.5" fill="#ff8800">
        <animate attributeName="cy"      values="14;-4;14"    dur="0.85s" repeatCount="indefinite"/>
        <animate attributeName="opacity" values="0.8;0;0.8"   dur="0.85s" repeatCount="indefinite"/>
        <animate attributeName="cx"      values="110;114;110" dur="0.85s" repeatCount="indefinite"/>
      </circle>
      <circle cx="98"  cy="8"  r="2.5" fill="#ffcc00">
        <animate attributeName="cy"      values="8;-10;8"     dur="1.4s" repeatCount="indefinite"/>
        <animate attributeName="opacity" values="0.9;0;0.9"   dur="1.4s" repeatCount="indefinite"/>
      </circle>

      <!-- "This is fine" dog -->
      <text x="10"  y="172" font-size="30">🐶</text>
      <text x="6"   y="186" font-size="7" fill="#555" font-family="Courier New">this is fine</text>
      <!-- Coffee mug beside dog -->
      <text x="154" y="186" font-size="20">&#x2615;</text>
    </svg>
    """.trimIndent()

// Generic — shrugging robot
private fun svgGeneric(code: Int) =
    """
    <svg width="200" height="170" viewBox="0 0 200 170" xmlns="http://www.w3.org/2000/svg">
      <!-- Body -->
      <rect x="68" y="88" width="64" height="58" rx="10" fill="#16163a" stroke="#4444aa" stroke-width="2"/>
      <text x="100" y="124" text-anchor="middle" font-size="18" fill="#ff4466" font-family="Courier New" font-weight="bold">$code</text>

      <!-- Legs -->
      <rect x="78"  y="144" width="18" height="22" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>
      <rect x="104" y="144" width="18" height="22" rx="6" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>

      <!-- Arms raised - shrug -->
      <rect x="28" y="76" width="42" height="16" rx="7" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(-28 49 84)"/>
      <rect x="130" y="76" width="42" height="16" rx="7" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5" transform="rotate(28 151 84)"/>
      <!-- Raised hands -->
      <ellipse cx="24"  cy="62" rx="10" ry="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>
      <ellipse cx="176" cy="62" rx="10" ry="8" fill="#1e1e3f" stroke="#4444aa" stroke-width="1.5"/>

      <!-- Head -->
      <rect x="66" y="20" width="68" height="68" rx="12" fill="#1e1e3f" stroke="#4444aa" stroke-width="2"/>
      <line x1="100" y1="20" x2="100" y2="6"  stroke="#888" stroke-width="3"/>
      <circle cx="100" cy="4" r="5" fill="#888">
        <animate attributeName="fill" values="#888;#aaa;#888" dur="2s" repeatCount="indefinite"/>
      </circle>

      <!-- ^^ eyes (confused happy) -->
      <path d="M76 52 Q86 44 96 52" stroke="#00ff88" stroke-width="3" fill="none" stroke-linecap="round"/>
      <path d="M104 52 Q114 44 124 52" stroke="#00ff88" stroke-width="3" fill="none" stroke-linecap="round"/>

      <!-- Mouth - uncertain -->
      <path d="M83 72 Q91 68 100 72 Q110 76 118 72" stroke="#888" stroke-width="2.5" fill="none" stroke-linecap="round"/>
    </svg>
    """.trimIndent()
