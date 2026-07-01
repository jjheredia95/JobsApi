import { Link } from 'react-router-dom'
import {useEffect, useState} from 'react'

export default function VacancyList() {

  const [vacancies, setVacancies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [successMessage, setSuccessMessage] = useState(location.state?.success || null)

  useEffect(() => {
    async function fetchVacancies() {
      try {
        const apiResponse = await fetch('http://localhost:8080/vacancies')

        if (!apiResponse.ok) {
          throw new Error('There was an error fetching the vacancies.')
        }

        const data = await apiResponse.json()
        setVacancies(data)
      } catch (error) {
        setError(error.message)
      } finally {
        setLoading(false)
      }
    }

    fetchVacancies()
  }, [])

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this vacancy?')
    if (!confirmDelete) return

    try {
      const response = await fetch(`http://localhost:8080/vacancies/delete/${id}`, {
        method: 'DELETE',
      })

      if (!response.ok) {
        throw new Error(`There was an error: ${response.status}`);
      }

      setSuccessMessage('Vacancy deleted successfully!')
      setVacancies((prev) => prev.filter((vacancy) => vacancy.id !== id))
    } catch (error) {
      setError(error.message)
    }
  }

  if (loading) {
    return (
        <main role="main" className="main-content">
          <div className="container py-5">
            <h2>Loading Vacancies...</h2>
          </div>
        </main>
    )
  }

  if (error) {
    return (
        <main role="main" className="main-content">
          <div className="container py-5">
            <h2>Error: {error}</h2>
          </div>
        </main>
    )
  }

  return (
    <main role="main" className="main-content">
      <div className="container">
        <div className="card app-card">
          <h4 className="card-header">
            <strong>List of Vacancies</strong>
          </h4>

          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}

            <Link className="btn btn-success" to="/vacancies/create" role="button">
              New
            </Link>

            <hr />

            {vacancies.length === 0 ? (
              <p className="mb-0">There are no vacancies available.</p>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover">
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">Category</th>
                      <th scope="col">Name</th>
                      <th scope="col">Open Date</th>
                      <th scope="col">Status</th>
                      <th scope="col">Featured</th>
                      <th scope="col">Operations</th>
                    </tr>
                  </thead>
                  <tbody>
                    {vacancies.map((vacancy) => (
                      <tr key={vacancy.id}>
                        <td>{vacancy.category}</td>
                        <td>{vacancy.name}</td>
                        <td>{vacancy.openDate}</td>
                        <td>{vacancy.status}</td>
                        <td>{vacancy.featured ? 'Yes' : 'No'}</td>
                        <td className="actions-cell">
                          <Link
                            to={`/vacancies/update/${vacancy.id}`}
                            className="btn btn-success btn-sm me-2"
                          >
                            Edit
                          </Link>
                          <button
                            type="button"
                            className="btn btn-danger btn-sm"
                            onClick={() => handleDelete(vacancy.id)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}