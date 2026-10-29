package id.ac.stmi.vehiclerent

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import id.ac.stmi.vehiclerent.model.DtcModel
import id.ac.stmi.vehiclerent.util.BottomNavHelper
import id.ac.stmi.vehiclerent.util.MotionUtils
import id.ac.stmi.vehiclerent.util.ObdParser
import id.ac.stmi.vehiclerent.util.ProfileDialogHelper
import id.ac.stmi.vehiclerent.util.VehicleRentPrefs
import java.text.NumberFormat
import java.util.Locale

class ObdDiagnosticActivity : AppCompatActivity() {

    private lateinit var prefs: VehicleRentPrefs
    private val idLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(idLocale)

    private lateinit var etObdSearchInput: EditText
    private lateinit var tvParsedSystemTitle: TextView
    private lateinit var tvParsedDescription: TextView
    private lateinit var tvParsedAction: TextView
    private lateinit var tvDigit1Dec: TextView
    private lateinit var tvDigit2Dec: TextView
    private lateinit var tvDigit3Dec: TextView
    private lateinit var tvParsedHours: TextView
    private lateinit var tvParsedCost: TextView

    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterP: Button
    private lateinit var btnFilterC: Button
    private lateinit var btnFilterB: Button
    private lateinit var btnFilterU: Button

    private lateinit var tvDictionaryCount: TextView
    private lateinit var llDtcListContainer: LinearLayout

    private var activeFilterSystem: String? = null
    private var allDictionary: List<DtcModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = VehicleRentPrefs.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obd_diagnostic)

        allDictionary = ObdParser.getDtcDictionary(this)
        initViews()
        renderRoleBanner()
        setupListeners()
        performLiveParse("P0300")
        renderDtcList()
    }

    override fun onResume() {
        super.onResume()
        ProfileDialogHelper.setupRoleBanner(this) {
            BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_OBD)
        }
        BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_OBD)
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<FrameLayout>(R.id.btnToolbarProfile).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                renderRoleBanner()
            }
        }

        findViewById<TextView>(R.id.btnQuickSwitchRole).setOnClickListener {
            ProfileDialogHelper.showProfileDialog(this) {
                renderRoleBanner()
            }
        }

        etObdSearchInput = findViewById(R.id.etObdSearchInput)
        tvParsedSystemTitle = findViewById(R.id.tvParsedSystemTitle)
        tvParsedDescription = findViewById(R.id.tvParsedDescription)
        tvParsedAction = findViewById(R.id.tvParsedAction)
        tvDigit1Dec = findViewById(R.id.tvDigit1Dec)
        tvDigit2Dec = findViewById(R.id.tvDigit2Dec)
        tvDigit3Dec = findViewById(R.id.tvDigit3Dec)
        tvParsedHours = findViewById(R.id.tvParsedHours)
        tvParsedCost = findViewById(R.id.tvParsedCost)

        btnFilterAll = findViewById(R.id.btnFilterAll)
        btnFilterP = findViewById(R.id.btnFilterP)
        btnFilterC = findViewById(R.id.btnFilterC)
        btnFilterB = findViewById(R.id.btnFilterB)
        btnFilterU = findViewById(R.id.btnFilterU)

        tvDictionaryCount = findViewById(R.id.tvDictionaryCount)
        llDtcListContainer = findViewById(R.id.llDtcListContainer)

        BottomNavHelper.setup(this, BottomNavHelper.NavTab.ADMIN_OBD)
    }

    private fun renderRoleBanner() {
        val bannerContainer = findViewById<LinearLayout>(R.id.llRoleBannerContainer) ?: return
        val ivIcon = findViewById<ImageView>(R.id.ivRoleBannerIcon)
        val tvTitle = findViewById<TextView>(R.id.tvRoleBannerTitle)
        val btnSwitch = findViewById<TextView>(R.id.btnQuickSwitchRole)

        if (prefs.isWorkshopAdmin) {
            bannerContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.workshop_amber_surface))
            ivIcon?.setColorFilter(ContextCompat.getColor(this, R.color.workshop_amber_dark))
            tvTitle?.text = "MODE PEMANGKU USAHA • ${prefs.activeUserName}"
            tvTitle?.setTextColor(ContextCompat.getColor(this, R.color.workshop_amber_dark))
            btnSwitch?.setBackgroundResource(R.drawable.bg_badge_workshop)
            btnSwitch?.setTextColor(ContextCompat.getColor(this, R.color.status_workshop_text))
        } else {
            bannerContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_navy_light))
            ivIcon?.setColorFilter(ContextCompat.getColor(this, R.color.primary_navy))
            tvTitle?.text = "MODE PELANGGAN • ${prefs.activeUserName}"
            tvTitle?.setTextColor(ContextCompat.getColor(this, R.color.primary_navy))
            btnSwitch?.setBackgroundResource(R.drawable.bg_badge_ready)
            btnSwitch?.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
        }
    }

    private fun setupListeners() {
        etObdSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val code = s?.toString()?.trim() ?: ""
                performLiveParse(code)
                filterDtcListBySearch(code)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val filterButtons = listOf(btnFilterAll, btnFilterP, btnFilterC, btnFilterB, btnFilterU)
        for (btn in filterButtons) {
            MotionUtils.applyTactilePressFeedback(btn)
        }

        btnFilterAll.setOnClickListener {
            activeFilterSystem = null
            highlightFilterButton(btnFilterAll)
            renderDtcList()
        }
        btnFilterP.setOnClickListener {
            activeFilterSystem = "Powertrain"
            highlightFilterButton(btnFilterP)
            renderDtcList()
        }
        btnFilterC.setOnClickListener {
            activeFilterSystem = "Chassis"
            highlightFilterButton(btnFilterC)
            renderDtcList()
        }
        btnFilterB.setOnClickListener {
            activeFilterSystem = "Body"
            highlightFilterButton(btnFilterB)
            renderDtcList()
        }
        btnFilterU.setOnClickListener {
            activeFilterSystem = "Network"
            highlightFilterButton(btnFilterU)
            renderDtcList()
        }
    }

    private fun highlightFilterButton(selectedBtn: Button) {
        val all = listOf(btnFilterAll, btnFilterP, btnFilterC, btnFilterB, btnFilterU)
        for (b in all) {
            if (b == selectedBtn) {
                b.setBackgroundResource(R.drawable.bg_button_primary)
                b.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                b.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
                b.setTextColor(ContextCompat.getColor(this, R.color.text_slate_600))
            }
        }
    }

    private fun performLiveParse(code: String) {
        val dtc = ObdParser.parseOrLookup(this, code)

        tvParsedSystemTitle.text = "${dtc.system} • ${dtc.category}"
        tvParsedDescription.text = dtc.description
        tvParsedAction.text = "Tindakan: ${dtc.recommendedAction}"

        val clean = dtc.code.uppercase()
        if (clean.length >= 3) {
            tvDigit1Dec.text = "D1: ${clean[0]} (${dtc.system})"
            tvDigit2Dec.text = if (clean[1] == '0') "D2: 0 (SAE Std)" else "D2: 1 (OEM Specific)"
            tvDigit3Dec.text = "D3: ${clean[2]} (Subsistem)"
        } else {
            tvDigit1Dec.text = "D1: P (Powertrain)"
            tvDigit2Dec.text = "D2: 0 (SAE Std)"
            tvDigit3Dec.text = "D3: 3 (Ignition)"
        }

        tvParsedHours.text = String.format(Locale.US, "Estimasi: %.1f Jam", dtc.flatRateHours)
        tvParsedCost.text = "Suku Cadang: Rp ${currencyFormat.format(dtc.partCost)}"
    }

    private fun filterDtcListBySearch(query: String) {
        val filtered = allDictionary.filter {
            val matchesSys = activeFilterSystem == null || it.system.equals(activeFilterSystem, true)
            val matchesQuery = query.isEmpty() ||
                it.code.contains(query, true) ||
                it.description.contains(query, true) ||
                it.category.contains(query, true)
            matchesSys && matchesQuery
        }
        populateDtcViews(filtered)
    }

    private fun renderDtcList() {
        val filtered = allDictionary.filter {
            activeFilterSystem == null || it.system.equals(activeFilterSystem, true)
        }
        populateDtcViews(filtered)
    }

    private fun populateDtcViews(items: List<DtcModel>) {
        llDtcListContainer.removeAllViews()
        tvDictionaryCount.text = "DATASET STANDAR SAE J2012 (${items.size} KODE)"

        for (item in items) {
            val view = layoutInflater.inflate(R.layout.item_dtc_row, llDtcListContainer, false)

            view.findViewById<TextView>(R.id.tvDtcCode).text = item.code
            view.findViewById<TextView>(R.id.tvDtcSystemCategory).text = "${item.system} • ${item.category}"
            view.findViewById<TextView>(R.id.tvDtcDescription).text = item.description
            view.findViewById<TextView>(R.id.tvDtcAction).text = "Rekomendasi: ${item.recommendedAction}"
            view.findViewById<TextView>(R.id.tvDtcHours).text =
                String.format(Locale.US, "Flat-Rate: %.1f Jam", item.flatRateHours)
            view.findViewById<TextView>(R.id.tvDtcCost).text =
                "Suku Cadang: Rp ${currencyFormat.format(item.partCost)}"

            val tvSeverity = view.findViewById<TextView>(R.id.tvDtcSeverity)
            tvSeverity.text = item.severity
            when (item.severity) {
                "CRITICAL" -> {
                    tvSeverity.setBackgroundResource(R.drawable.bg_badge_workshop)
                    tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.status_workshop_text))
                }
                "WARNING" -> {
                    tvSeverity.setBackgroundResource(R.drawable.bg_badge_warning)
                    tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.workshop_amber_dark))
                }
                else -> {
                    tvSeverity.setBackgroundResource(R.drawable.bg_badge_ready)
                    tvSeverity.setTextColor(ContextCompat.getColor(this, R.color.status_ready_text))
                }
            }

            MotionUtils.applyTactilePressFeedback(view)
            view.setOnClickListener {
                if (intent.getBooleanExtra("SELECT_MODE", false)) {
                    val resIntent = Intent().apply {
                        putExtra("SELECTED_DTC_CODE", item.code)
                    }
                    setResult(RESULT_OK, resIntent)
                    finish()
                } else {
                    etObdSearchInput.setText(item.code)
                    performLiveParse(item.code)
                }
            }

            llDtcListContainer.addView(view)
        }
    }
}

