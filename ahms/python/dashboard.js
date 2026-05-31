export default function AHMSDashboard() {
  const stats = [
    { title: 'Total Patients', value: '154' },
    { title: 'Doctors Available', value: '32' },
    { title: 'ICU Beds', value: '8' },
    { title: 'Ambulances', value: '5' },
  ];

  const patients = [
    {
      id: 1,
      name: 'Rahul Sharma',
      disease: 'Fever',
      room: 'A-101',
      status: 'Recovering',
    },
    {
      id: 2,
      name: 'Priya Verma',
      disease: 'Diabetes',
      room: 'B-204',
      status: 'Critical',
    },
    {
      id: 3,
      name: 'Amit Singh',
      disease: 'Heart Disease',
      room: 'ICU-2',
      status: 'Stable',
    },
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex">
      {/* Sidebar */}
      <div className="w-72 bg-slate-900 text-white p-6 shadow-2xl">
        <h1 className="text-3xl font-bold mb-10">AHMS</h1>

        <nav className="space-y-4">
          <button className="w-full text-left px-4 py-3 rounded-xl bg-blue-600 hover:bg-blue-500 transition">
            Dashboard
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            Patients
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            Doctors
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            Appointments
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            Ambulance
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            ICU Beds
          </button>

          <button className="w-full text-left px-4 py-3 rounded-xl hover:bg-slate-800 transition">
            Emergency
          </button>
        </nav>
      </div>

      {/* Main Content */}
      <div className="flex-1 p-8 overflow-auto">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h2 className="text-4xl font-bold text-slate-800">
              Hospital Dashboard
            </h2>
            <p className="text-slate-500 mt-1">
              Advanced Hospital Management System
            </p>
          </div>

          <div className="flex items-center gap-4">
            <input
              type="text"
              placeholder="Search..."
              className="px-4 py-3 rounded-xl border border-slate-300 outline-none w-72"
            />

            <div className="w-12 h-12 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-lg">
              A
            </div>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6 mb-10">
          {stats.map((item, index) => (
            <div
              key={index}
              className="bg-white p-6 rounded-3xl shadow-lg hover:scale-105 transition"
            >
              <h3 className="text-slate-500 text-lg">{item.title}</h3>
              <p className="text-4xl font-bold mt-4 text-slate-800">
                {item.value}
              </p>
            </div>
          ))}
        </div>

        {/* Main Grid */}
        <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
          {/* Patient Table */}
          <div className="xl:col-span-2 bg-white rounded-3xl shadow-lg p-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-slate-800">
                Recent Patients
              </h3>

              <button className="bg-blue-600 text-white px-5 py-2 rounded-xl hover:bg-blue-500 transition">
                Add Patient
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="text-left border-b border-slate-200">
                    <th className="pb-4">Patient</th>
                    <th className="pb-4">Disease</th>
                    <th className="pb-4">Room</th>
                    <th className="pb-4">Status</th>
                  </tr>
                </thead>

                <tbody>
                  {patients.map((patient) => (
                    <tr
                      key={patient.id}
                      className="border-b border-slate-100 hover:bg-slate-50"
                    >
                      <td className="py-4 font-medium">{patient.name}</td>
                      <td>{patient.disease}</td>
                      <td>{patient.room}</td>
                      <td>
                        <span className="px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-700">
                          {patient.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Right Panel */}
          <div className="space-y-8">
            {/* Emergency */}
            <div className="bg-red-500 text-white p-6 rounded-3xl shadow-lg">
              <h3 className="text-2xl font-bold mb-4">Emergency Alert</h3>

              <p className="text-lg">
                ICU occupancy has reached 85%.
              </p>

              <button className="mt-6 bg-white text-red-500 px-5 py-2 rounded-xl font-semibold hover:bg-slate-100 transition">
                View Details
              </button>
            </div>

            {/* Doctors */}
            <div className="bg-white p-6 rounded-3xl shadow-lg">
              <h3 className="text-2xl font-bold mb-6 text-slate-800">
                Available Doctors
              </h3>

              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <div>
                    <p className="font-semibold">Dr. Mehta</p>
                    <p className="text-sm text-slate-500">Cardiologist</p>
                  </div>

                  <span className="text-green-600 font-semibold">
                    Available
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <div>
                    <p className="font-semibold">Dr. Khan</p>
                    <p className="text-sm text-slate-500">Neurologist</p>
                  </div>

                  <span className="text-yellow-600 font-semibold">
                    Busy
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <div>
                    <p className="font-semibold">Dr. Sharma</p>
                    <p className="text-sm text-slate-500">Orthopedic</p>
                  </div>

                  <span className="text-green-600 font-semibold">
                    Available
                  </span>
                </div>
              </div>
            </div>

            {/* Ambulance */}
            <div className="bg-white p-6 rounded-3xl shadow-lg">
              <h3 className="text-2xl font-bold mb-4 text-slate-800">
                Ambulance Status
              </h3>

              <div className="space-y-3">
                <div className="flex justify-between">
                  <span>AMB-101</span>
                  <span className="text-green-600 font-semibold">
                    Available
                  </span>
                </div>

                <div className="flex justify-between">
                  <span>AMB-102</span>
                  <span className="text-red-600 font-semibold">
                    Emergency
                  </span>
                </div>

                <div className="flex justify-between">
                  <span>AMB-103</span>
                  <span className="text-green-600 font-semibold">
                    Available
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
