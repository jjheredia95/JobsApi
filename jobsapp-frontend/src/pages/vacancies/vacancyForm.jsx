import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

const today = new Date().toISOString().split('T')[0]

//Initial values for the form when creating a new vacancy
const initialForm = {
  name: '',
  description: '',
  category: '',
  status: 'OPEN',
  openDate: today,
  featured: '1',
  salary: '',
  details: '',
}

//select options for the status field
const statusOptions = ['OPEN', 'CLOSED', 'DELETED']

export default function VacancyForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id) // let's save true or false if the vacancy is being edited or created

  const [form, setForm] = useState(initialForm)
  const [imageFile, setImageFile] = useState(null)
  const [imagePreview, setImagePreview] = useState(null)
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchCategories = fetch('http://localhost:8080/categories')
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP error: ${res.status}`)
        return res.json()
      })
      .then((data) => setCategories(data))

    const fetchVacancy = isEdit
      ? fetch(`http://localhost:8080/vacancies/${id}`)
          .then((res) => {
            if (!res.ok) throw new Error(`HTTP error: ${res.status}`)
            return res.json()
          })
          .then((data) => {
            setForm({
              name: data.name ?? '',
              description: data.description ?? '',
              category: data.category ?? '',
              status: data.status ?? 'OPEN',
              openDate: data.openDate ?? '',
              featured: data.featured ? '1' : '0',
              salary: data.salary ?? '',
              details: data.details ?? '',
            })
            if (data.imageUrl) setImagePreview(data.imageUrl)
          })
      : Promise.resolve()

    Promise.all([fetchCategories, fetchVacancy])
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleFileChange = (e) => {
    const file = e.target.files?.[0] ?? null
    setImageFile(file)
    setImagePreview(file ? URL.createObjectURL(file) : null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)

    try {
      const url = isEdit
        ? `http://localhost:8080/vacancies/update/${id}`
        : 'http://localhost:8080/vacancies/create'

      const method = isEdit ? 'PUT' : 'POST'

      const formData = new FormData()
      Object.entries(form).forEach(([key, value]) => formData.append(key, value))
      if (imageFile) formData.append('imageFile', imageFile)

      const response = await fetch(url, { method, body: formData })

      if (!response.ok) {
        let message = `HTTP error: ${response.status}`
        try {
          const data = await response.json()
          if (typeof data.message === 'object' && data.message !== null) {
            message = Object.entries(data.message)
              .map(([field, msg]) => `${field}: ${msg}`)
              .join(', ') || data.error || message
          } else {
            message = data.message || data.error || message
          }
        } catch { /* non-JSON response */ }
        throw new Error(message)
      }

      navigate('/vacancies', {
        state: { success: isEdit ? 'Vacancy updated successfully' : 'Vacancy created successfully' },
      })
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
          <h2>Loading vacancy...</h2>
        </div>
      </main>
    )
  }

  return (
    <main role="main" className="main-content">
      <div className="container">
        <div className="card app-card">
          <h4 className="card-header">
            <strong>{isEdit ? 'Edit Job Vacancy' : 'Create Job Vacancy'}</strong>
          </h4>

          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="name">Name</label>
                    <input
                      type="text"
                      className="form-control"
                      id="name"
                      name="name"
                      placeholder="Job vacancy title"
                      value={form.name}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="col-md-9">
                  <div className="form-group mb-3">
                    <label htmlFor="description">Description</label>
                    <input
                      type="text"
                      className="form-control"
                      id="description"
                      name="description"
                      placeholder="Short description of the job vacancy"
                      value={form.description}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="row">
                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="category">Category</label>
                    <select
                      className="form-control"
                      name="category"
                      id="category"
                      value={form.category}
                      onChange={handleChange}
                      required
                    >
                      <option value="">-- Select a category --</option>
                      {categories.map((cat) => (
                        <option key={cat.id} value={cat.name}>
                          {cat.name}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="status">Status</label>
                    <select
                      className="form-control"
                      name="status"
                      id="status"
                      value={form.status}
                      onChange={handleChange}
                    >
                      {statusOptions.map((status) => (
                        <option key={status} value={status}>
                          {status}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="openDate">Published Date</label>
                    <input
                      type="date"
                      className="form-control"
                      name="openDate"
                      id="openDate"
                      placeholder="Publication date"
                      value={form.openDate}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label className="d-block">Featured</label>
                    <div className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="featured"
                        id="featured-1"
                        value="1"
                        checked={form.featured === '1'}
                        onChange={handleChange}
                      />
                      <label className="form-check-label" htmlFor="featured-1">
                        Featured
                      </label>
                    </div>
                    <div className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="featured"
                        id="featured-0"
                        value="0"
                        checked={form.featured === '0'}
                        onChange={handleChange}
                      />
                      <label className="form-check-label" htmlFor="featured-0">
                        Not Featured
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              <div className="row">
                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="salary">Estimated Salary</label>
                    <input
                      type="text"
                      className="form-control"
                      name="salary"
                      id="salary"
                      placeholder="Approximate salary"
                      value={form.salary}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="col-md-3">
                  <div className="form-group mb-3">
                    <label htmlFor="imageFile">Image</label>
                    <input
                      type="file"
                      className="form-control"
                      name="imageFile"
                      id="imageFile"
                      onChange={handleFileChange}
                    />
                  </div>
                </div>
              </div>

              <hr />

              <div className="row">
                <div className="col-md-9">
                  <div className="form-group mb-3">
                    <label htmlFor="details">
                      <strong>Details</strong>
                    </label>
                    <textarea
                      className="form-control"
                      name="details"
                      id="details"
                      rows="7"
                      value={form.details}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="col-md-3">
                  <label>Image / Logo</label>
                  <img
                    className="rounded mx-auto d-block"
                    src={imagePreview || '/images/construction-logo.png'}
                    alt="Job preview"
                    width="200"
                    height="200"
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Saving...' : 'Save'}
              </button>

              <button
                type="button"
                className="btn btn-secondary ms-2"
                onClick={() => navigate('/vacancies')}
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
