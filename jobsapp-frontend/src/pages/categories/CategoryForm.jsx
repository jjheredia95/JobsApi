import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

const initialForm = {
  name: '',
  description: '',
}

export default function CategoryForm() {

  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [form, setForm] = useState(initialForm)
  const [loading, setLoading] = useState(isEdit)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!isEdit) return

    fetch(`http://localhost:8080/categories/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP error: ${res.status}`)
        return res.json()
      })
      .then((data) => {
        setForm({
          name: data.name ?? '',
          description: data.description ?? '',
        })
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)

    try {
      const url = isEdit
          ? `http://localhost:8080/categories/update/${id}`
          : 'http://localhost:8080/categories/create'

      const method = isEdit ? 'PUT' : 'POST'

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(form),
      })

      if (!response.ok) {
        let message = `HTTP error: ${response.status}`

        try {
          const data = await response.json()
          if (typeof data.message === 'object' && data.message !== null) {
            // Extrae los errores del objeto y únelos en un string legible
            const errors = Object.entries(data.message)
                .map(([field, msg]) => `${field}: ${msg}`)
                .join(', ');
            message = errors || data.error || message;
          } else {
            message = data.message || data.error || message;
          }
        } catch {
          // Si el backend no devuelve JSON, se usa el mensaje genérico
        }

        throw new Error(message)
      }

      navigate('/categories', { state: { success: isEdit ? 'Category updated successfully' : 'Category created successfully' } })
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <main role="main" className="main-content">
        <div className="container py-5">
          <h2>Loading category...</h2>
        </div>
      </main>
    )
  }

  return (
    <main role="main" className="main-content">
      <div className="container">
        <div className="card">
          <h4 className="card-header">
            <strong>{isEdit ? 'Edit Category' : 'Create Category'}</strong>
          </h4>

          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <div className="form-group mb-3">
                    <label htmlFor="name">Name</label>
                    <input
                      type="text"
                      className="form-control"
                      id="name"
                      name="name"
                      value={form.name}
                      onChange={handleChange}
                      placeholder="Write the category name"
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="row">
                <div className="col-md-6">
                  <div className="form-group mb-3">
                    <label htmlFor="description">Description</label>
                    <textarea
                      className="form-control"
                      id="description"
                      name="description"
                      rows="2"
                      value={form.description}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>

              <hr />

              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Saving...' : 'Save'}
              </button>

              <button
                type="button"
                className="btn btn-secondary ms-2"
                onClick={() => navigate('/categories')}
              >
                Cancel
              </button>
            </form>
          </div>
        </div>
      </div>
    </main>
  )
}