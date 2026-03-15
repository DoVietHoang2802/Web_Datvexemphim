/**
 * Admin Modal Forms Utility
 * Provides reusable modal functions for admin pages
 */

// Get API_BASE from config
const MODALS_API_BASE = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE)
  ? CONFIG.API_BASE
  : "http://localhost:9090/api";

export async function showMovieModal(defaults, onSubmit) {
  const modalId = "movieModal";

  // Lấy danh sách genres từ API (dùng endpoint công khai)
  let genresOptions = '<option value="">-- Chọn thể loại --</option>';
  try {
    // Dùng endpoint công khai, không cần token
    const response = await fetch(`${MODALS_API_BASE}/movies/genres`);
    if (response.ok) {
      const genres = await response.json();
      genresOptions += genres.map(g =>
        `<option value="${g.id}" ${defaults?.genreId == g.id ? 'selected' : ''}>${g.name}</option>`
      ).join('');
    }
  } catch (e) {
    console.error("Error loading genres:", e);
    // Fallback nếu không load được
    genresOptions = `
      <option value="">-- Chọn thể loại --</option>
      <option value="1">Hành động</option>
      <option value="2">Tình cảm</option>
      <option value="3">Hài hước</option>
      <option value="4">Kinh dị</option>
      <option value="5">Khoa học viễn tưởng</option>
      <option value="6">Phiêu lưu</option>
      <option value="7">Hoạt hình</option>
      <option value="8">Khác</option>
    `;
  }

  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa phim' : 'Thêm phim'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="movieForm">
              <div class="mb-3">
                <label class="form-label">Tên phim</label>
                <input type="text" class="form-control" name="title" value="${defaults?.title ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Thời lượng (phút)</label>
                <input type="number" class="form-control" name="durationMinutes" value="${defaults?.durationMinutes ?? 120}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mô tả</label>
                <textarea class="form-control" name="description" rows="3">${defaults?.description ?? ''}</textarea>
              </div>
              <div class="mb-3">
                <label class="form-label">URL poster</label>
                <input type="url" class="form-control" name="posterUrl" value="${defaults?.posterUrl ?? ''}">
              </div>
              <div class="mb-3">
                <label class="form-label">URL trailer</label>
                <input type="url" class="form-control" name="trailerUrl" value="${defaults?.trailerUrl ?? ''}">
              </div>
              <div class="mb-3">
                <label class="form-label">Xếp hạng</label>
                <input type="text" class="form-control" name="rating" value="${defaults?.rating ?? ''}" placeholder="PG-13, R, etc.">
              </div>
              <div class="mb-3">
                <label class="form-label">Thể loại</label>
                <select class="form-select" name="genreId">
                  ${genresOptions}
                </select>
              </div>
              <div class="mb-3">
                <label class="form-check-label">
                  <input type="checkbox" class="form-check-input" name="active" ${defaults?.active ? 'checked' : ''}>
                  Kích hoạt
                </label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  `;

  showModalWithForm(html, modalId, "movieForm", "submitBtn", onSubmit);
}

export function showRoomModal(defaults, onSubmit) {
  const modalId = "roomModal";
  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa phòng' : 'Thêm phòng'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="roomForm">
              <div class="mb-3">
                <label class="form-label">Tên phòng</label>
                <input type="text" class="form-control" name="name" value="${defaults?.name ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Số hàng</label>
                <input type="number" class="form-control" name="totalRows" value="${defaults?.totalRows ?? 8}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Số cột</label>
                <input type="number" class="form-control" name="totalCols" value="${defaults?.totalCols ?? 10}" required>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  `;

  showModalWithForm(html, modalId, "roomForm", "submitBtn", onSubmit);
}

export function showShowtimeModal(defaults, movies = [], rooms = [], onSubmit) {
  try {
    const modalId = "showtimeModal";
    const movieOptions = movies.map(m => `<option value="${m.id}">${m.title}</option>`).join("");
    const roomOptions = rooms.map(r => `<option value="${r.id}">${r.name}</option>`).join("");
    
    const html = `
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${defaults?.id ? 'Sửa suất chiếu' : 'Thêm suất chiếu'}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <form class="admin-form" id="showtimeForm">
                <div class="mb-3">
                  <label class="form-label">Phim</label>
                  <select class="form-select" name="movieId" required>
                    <option value="">Chọn phim...</option>
                    ${movieOptions}
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Phòng</label>
                  <select class="form-select" name="roomId" required>
                    <option value="">Chọn phòng...</option>
                    ${roomOptions}
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Bắt đầu</label>
                  <input type="text" class="form-control flatpickr-datetime" id="startTimeInput" name="startTime" placeholder="Chọn ngày và giờ" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Kết thúc</label>
                  <input type="text" class="form-control flatpickr-datetime" id="endTimeInput" name="endTime" placeholder="Chọn ngày và giờ" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Giá (VND)</label>
                  <input type="number" class="form-control" name="price" value="${defaults?.price ?? 90000}" required>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector("#showtimeForm");
    const submitBtn = tempDiv.querySelector("#submitBtn");
    
    if (!modalEl || !form || !submitBtn) {
      console.error("Modal elements not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    // Load flatpickr
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/flatpickr';
    script.onload = () => {
      const startInput = tempDiv.querySelector("#startTimeInput");
      const endInput = tempDiv.querySelector("#endTimeInput");
      
      // Initialize flatpickr for start time
      flatpickr(startInput, {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        time_24hr: true,
        minuteIncrement: 5,
        defaultValue: defaults?.startTime ? new Date(defaults.startTime) : null,
        locale: "vi"
      });
      
      // Initialize flatpickr for end time
      flatpickr(endInput, {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        time_24hr: true,
        minuteIncrement: 5,
        defaultValue: defaults?.endTime ? new Date(defaults.endTime) : null,
        locale: "vi"
      });
    };
    document.head.appendChild(script);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        const startTimeVal = tempDiv.querySelector("#startTimeInput").value;
        const endTimeVal = tempDiv.querySelector("#endTimeInput").value;
        
        if (!startTimeVal || !endTimeVal) {
          alert("Vui lòng chọn ngày và giờ bắt đầu/kết thúc");
          return;
        }

        const formData = new FormData(form);
        const data = {
          movieId: Number(formData.get("movieId")),
          roomId: Number(formData.get("roomId")),
          startTime: new Date(startTimeVal).toISOString(),
          endTime: new Date(endTimeVal).toISOString(),
          price: Number(formData.get("price"))
        };
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in form submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showShowtimeModal:", e);
  }
}

export function showConfirmModal(title, message, onConfirm, confirmText = "Xác nhận", confirmClass = "btn-admin-danger") {
  try {
    const modalId = "confirmModal";
    const html = `
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <p>${message}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin ${confirmClass}" id="confirmBtn">${confirmText}</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    if (!modalEl) {
      console.error("Modal element not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);
    const confirmBtn = modalEl.querySelector("#confirmBtn");
    
    if (!confirmBtn) {
      console.error("Confirm button not found");
      tempDiv.remove();
      return;
    }

    confirmBtn.addEventListener("click", () => {
      try {
        bsModal.hide();
        onConfirm();
      } catch (e) {
        console.error("Error in onConfirm:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showConfirmModal:", e);
  }
}

function showModalWithForm(html, modalId, formId, submitBtnId, onSubmit) {
  try {
    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector(`#${formId}`);
    const submitBtn = tempDiv.querySelector(`#${submitBtnId}`);

    if (!modalEl) {
      console.error(`Modal element with id ${modalId} not found`);
      tempDiv.remove();
      return;
    }
    
    if (!form) {
      console.error(`Form with id ${formId} not found`);
      tempDiv.remove();
      return;
    }
    
    if (!submitBtn) {
      console.error(`Submit button with id ${submitBtnId} not found`);
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        const formData = new FormData(form);
        const data = Object.fromEntries(formData);

        // Handle checkbox for active field - convert "on" to true/false
        if (formId.includes("movie")) {
          data.active = formData.has("active") ? true : false;
        }

        // Handle checkbox for room if needed
        if (formId.includes("room")) {
          data.active = formData.has("active") ? true : false;
        }

        // Handle checkbox for genre (isActive field)
        if (formId.includes("genre")) {
          data.isActive = formData.has("isActive") ? true : false;
        }

        console.log("Form data:", data);
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in form submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showModalWithForm:", e);
  }
}

export function showGenerateSeatsModal(roomId, onSubmit) {
  try {
    const modalId = "generateSeatsModal";
    const html = `
      <div class="modal fade" id="${modalId}" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Tạo ghế cho phòng</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <form class="admin-form" id="generateSeatsForm">
                <div class="mb-3">
                  <label class="form-label">Số hàng</label>
                  <input type="number" class="form-control" name="totalRows" value="8" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Số cột</label>
                  <input type="number" class="form-control" name="totalCols" value="10" required>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
              <button type="button" class="btn btn-admin btn-admin-success" id="generateSubmitBtn">Tạo</button>
            </div>
          </div>
        </div>
      </div>
    `;

    const container = document.body;
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    container.appendChild(tempDiv);

    const modalEl = tempDiv.querySelector(`#${modalId}`);
    const form = tempDiv.querySelector("#generateSeatsForm");
    const submitBtn = tempDiv.querySelector("#generateSubmitBtn");

    if (!modalEl || !form || !submitBtn) {
      console.error("Modal elements not found");
      tempDiv.remove();
      return;
    }

    const bsModal = new window.bootstrap.Modal(modalEl);

    submitBtn.addEventListener("click", (e) => {
      try {
        e.preventDefault();
        e.stopPropagation();
        
        const formData = new FormData(form);
        const data = {
          roomId,
          totalRows: Number(formData.get("totalRows")),
          totalCols: Number(formData.get("totalCols"))
        };
        bsModal.hide();
        onSubmit(data);
      } catch (e) {
        console.error("Error in generate seats submit:", e);
      }
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      tempDiv.remove();
    });

    bsModal.show();
  } catch (e) {
    console.error("Error in showGenerateSeatsModal:", e);
  }
}

export function showGenreModal(defaults, onSubmit) {
  const modalId = "genreModal";
  const html = `
    <div class="modal fade" id="${modalId}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">${defaults?.id ? 'Sửa thể loại' : 'Thêm thể loại'}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form class="admin-form" id="genreForm">
              <div class="mb-3">
                <label class="form-label">Tên thể loại</label>
                <input type="text" class="form-control" name="name" value="${defaults?.name ?? ''}" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Mô tả</label>
                <textarea class="form-control" name="description" rows="2">${defaults?.description ?? ''}</textarea>
              </div>
              <div class="mb-3">
                <label class="form-check-label">
                  <input type="checkbox" class="form-check-input" name="isActive" ${defaults?.isActive !== false ? 'checked' : ''}>
                  Kích hoạt
                </label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-admin btn-outline-light" data-bs-dismiss="modal">Hủy</button>
            <button type="button" class="btn btn-admin btn-admin-primary" id="submitBtn">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  `;

  showModalWithForm(html, modalId, "genreForm", "submitBtn", onSubmit);
}
