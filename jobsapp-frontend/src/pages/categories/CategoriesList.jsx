import { useEffect, useState } from 'react'
import {Link, useLocation} from "react-router-dom";

export default function CategoriesList() {

  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const location = useLocation()
  const [successMessage, setSuccessMessage] = useState(location.state?.success || null)

  useEffect(() => {
    fetch(`http://localhost:8080/categories`)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP error: ${res.status}`)
        return res.json()
      })
      .then(setCategories)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this category?')
    if (!confirmDelete) return
      try {
        const res = await fetch(`http://localhost:8080/categories/delete/${id}`, {
          method: 'DELETE',
        })

        if (!res.ok) {
          throw new Error(`HTTP error: ${res.status}`)
        }

        setCategories((prev) => prev.filter((category) => category.id !== id))
        setSuccessMessage('Category deleted successfully.')
      } catch (err) {
        setError(err.message)
      }


  }

  if (loading) {
    return (
      <main role="main" className="main-content">
        <div className="container py-5">
          <h2>Loading Categories...</h2>
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
            <strong>List of Categories</strong>
          </h4>

          <div className="card-body">
            {successMessage && (
              <div className="alert alert-success mb-3" role="alert">
                {successMessage}
              </div>
            )}

            <Link className="btn btn-success" to={`/categories/create`} role="button">New</Link>

            <hr />

            {categories.length === 0 ? (
              <p className="mb-0">There are not categories available.</p>
            ) : (
              <table className="table table-hover">
                <thead className="thead-light">
                  <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Name</th>
                    <th scope="col">Description</th>
                    <th scope="col">Operations</th>
                  </tr>
                </thead>
                <tbody>
                  {categories.map((category) => (
                    <tr key={category.id}>
                      <th scope="row">{category.id}</th>
                      <td>{category.name}</td>
                      <td>{category.description}</td>
                      <td className="actions-cell">
                        <Link to={`/categories/update/${category.id}`} className="btn btn-success btn-sm me-2">
                          Edit
                        </Link>
                        <button type="button" className="btn btn-danger btn-sm" onClick={() => handleDelete(category.id)}>
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}