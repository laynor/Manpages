package mrz.android.manpages

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Adapter
import kotlin.properties.Delegates
import android.widget.Toast
import android.widget.Button
import android.net.Uri
import io.realm.Realm
import mrz.android.manpages.model.Archive
import io.realm.RealmResults

open class WelcomeFragment : Fragment() {

    private val mRealm: Realm? by Delegates.lazy {
        Realm.getInstance(getActivity().getApplicationContext())
    }

    private val mProjectSpinner: Spinner? by Delegates.lazy {
        getView().findViewById(R.id.project_spinner) as Spinner?
    }

    private val mVersionSpinner: Spinner? by Delegates.lazy {
        getView().findViewById(R.id.version_spinner) as Spinner?
    }

    private val mConfirmButton: Button? by Delegates.lazy {
        getView().findViewById(R.id.confirm_button) as Button?
    }

    private val mProjects: RealmResults<Archive>? by Delegates.lazy {
        mRealm?.where(javaClass<Archive>())?.findAll()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_welcome, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectAdapter: SpinnerAdapter = SpinnerAdapter(getActivity())
        projectAdapter.setItems(
                listOf(*getActivity().getResources().getTextArray(R.array.distributions_array)))

        val versionAdapter: SpinnerAdapter = SpinnerAdapter(getActivity())

        mProjectSpinner?.setAdapter(projectAdapter)
        mVersionSpinner?.setAdapter(versionAdapter)

        // Avoid triggering an onItemSelected when setting the adapter
        listOf(mProjectSpinner, mVersionSpinner) map { s ->
            s?.setSelection(0, false)
        }

        mProjectSpinner?.setOnItemSelectedListener(
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<out Adapter>?, view: View?,
                            position: Int, id: Long) {
                        when (parent?.getItemAtPosition(position) as CharSequence) {
                            "Linux" -> populateAdapter(versionAdapter,
                                    mProjects?.where()?.equalTo("project", "Linux")?.findAll())
                            "FreeBSD" -> populateAdapter(versionAdapter,
                                    mProjects?.where()?.equalTo("project", "FreeBSD")?.findAll())
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                    }
                })

        mVersionSpinner?.setOnItemSelectedListener(
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<out Adapter>?, view: View?,
                            position: Int, id: Long) {
                        Toast.makeText(getActivity(),
                                "Selected ${parent?.getItemAtPosition(position)}",
                                Toast.LENGTH_LONG).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                    }
                })

        mConfirmButton?.setOnClickListener {
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    val distribution = mProjectSpinner?.getSelectedItem() as String
                    val version = mVersionSpinner?.getSelectedItem() as String

                    val downloadURL: Uri = generateDownloadURL(distribution, version)

                    Toast.makeText(getActivity(),
                            "Download URL: ${downloadURL}", Toast.LENGTH_LONG).show()

                    //EventBus.getDefault().post(StartDownloadEvent(downloadURL))
                }
            }
        }
    }

    private fun populateAdapter(adapter: SpinnerAdapter, items: RealmResults<Archive>?) {
        adapter.clear()
        adapter.setItems(items?.map { it -> it.getVersion() })
        adapter.notifyDataSetChanged()
    }

    private fun generateDownloadURL(distribution: String, version: String): Uri {
        val uri = mRealm?.where(javaClass<Archive>())?.equalTo("project", distribution)?.equalTo(
                "version", version)?.findFirst()

        return Uri.parse(uri?.getURI())
    }
}