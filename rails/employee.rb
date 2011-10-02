require 'sinatra'
require 'dm-core'
require 'rails/model/letter'
require 'rails/model/date'

# Configure DataMapper to use the App Engine datastore 
DataMapper.setup(:default, "appengine://auto")

# Make sure our template can use <%=h
helpers do
  include Rack::Utils
  alias_method :h, :escape_html
end

get '/employee' do
  # Just list all the shouts
  @letters = Letter.all
  erb :employee_index
end

# post '/' do
#   # Create a new shout and redirect back to the list.
#   letter = Letter.create(:id=>params[:id],:create_date=>params[:create_date],:employee_id=>params[:employee_id],
#     :country=>params[:country],:type=>params[:type],:note=>params[:note],:status=>params[:status],
#     :voulenteer_id=>params[:voulenteer_id],:voulentter_name=>params[:voulentter_name],
#     :claim_date=>params[:claim_date],:due_date=>params[:due_date],:upload_file=>params[:upload_file],
#     :return_file=>params[:return_file]
#   )
#   redirect '/'
# end

post '/update_letter' do
    id = params[:id]
    if (!id.nil?)
      letter = Letter.get(id)
      puts "debug" + letter.to_s
      if !params[:country].nil?
        puts "debug: save: country"
        letter.country = params[:country]
      end
      if !params[:type].nil?
        puts "debug: save: type"
        letter.type = params[:type]
      end
      if !params[:note].nil?
        puts "debug: save: note"
        letter.note = params[:note]
      end
      if !params[:number_of_letters].nil?
        puts "debug: save: letters"
        letter.number_of_letters = params[:number_of_letters]
      end
      letter.save
    end
    redirect '/employee'
end

post '/delete_letter' do
    id = params[:id]
    if (!id.nil?)
      letter = Letter.get(id)
      if (!letter.nil?)
        letter.destroy
      end
    end
    redirect '/employee'
end

__END__

@@ employee_index
